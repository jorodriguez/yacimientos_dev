package sia.servicios.catalogos.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.Gerencia;
import sia.modelo.OcFlujo;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.SiRol;
import sia.modelo.SiUsuarioRol;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.EmpleadoMaterialVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.CompaniaVo;
import sia.notificaciones.sistema.impl.ServicioNotificacionSistemaImpl;
import sia.notificaciones.usuario.impl.ServicioNotificacionUsuarioImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.comunicacion.impl.CoNoticiaUsuarioImpl;
import sia.servicios.orden.impl.OcFlujoImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgEmpresaImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.viaje.impl.SgUsuarioRolGerenciaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.usuario.impl.RhTipoGerenciaImpl;
import sia.util.UtilLog4j;
import sia.util.UtilSia;
import javax.ejb.Stateless;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.sierr@gmail.com @date 7/07/2009
 */
@Stateless
@Slf4j
public class UsuarioImpl extends AbstractFacade<Usuario> {

    
    private static final String CONSULTA = 
            "SELECT u.id, u.nombre, u.clave, u.email,  u.destinatarios, u.telefono, u.extension, "
                + "  u.sexo, u.celular, u.fechanacimiento, u.rfc, u.compania, u.foto, u.pregunta_secreta,"
                + "  u.respuesta_pregunta_secreta, u.activo, u.genero,"
                + "  (select c.nombre from ap_campo c where u.ap_campo = c.id), "
                + "  (select g.nombre from gerencia g where u.gerencia is not null and u.gerencia = g.id),"
                + "  u.gerencia, u.ap_campo,u.sg_oficina"
                + " FROM usuario u ";
    
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Inject
    DSLContext dslCtx;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    //
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private ServicioNotificacionSistemaImpl notificacionSistemaRemote;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private ServicioNotificacionUsuarioImpl notificacionUsuarioRemote;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgEmpresaImpl sgEmpresaRemote;
    @Inject
    private SiUsuarioTipoImpl siUsuarioTipoRemote;
    @Inject
    private CoNoticiaUsuarioImpl coNoticiaUsuarioRemote;
    @Inject
    private CoNoticiaImpl coNoticiaRemote;
    @Inject
    private RhTipoGerenciaImpl rhTipoGerenciaRemote;
    @Inject
    private SgEstatusAprobacionImpl estatusAprobacion;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SgUsuarioRolGerenciaImpl sgUsuarioRolGerenciaRemote;
    @Inject
    private OcFlujoImpl ocFlujoRemote;
    //

    @Inject
    DSLContext dbCtx;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    

    public UsuarioImpl() {
        super(Usuario.class);
    }

    
    public Usuario find(String id) {
        try {
            return (Usuario) em.createNamedQuery("Usuario.findById").setParameter(1, id).getSingleResult();
        } catch (Exception e) {
            log.warn("No encontró el usuario {} ", id, e);
            return null;
        }

    }
    
    
    public Usuario findRH(String id) {
        try { 
            return (Usuario) em.createNamedQuery("Usuario.findByIdRH").setParameter(1, id).getSingleResult();
        } catch (Exception e) {
            log.warn("No encontró el usuario {}", id , e);
            return null;
        }

    }

    /**
     * Devuelve el texto con la primera letra mayúscula y las demás en
     * minúsculas
     *
     * @param text
     * @return
     */
    public String transformTextToCamelNotation(String text) {
        String textConverted = "";
        if (!text.isEmpty()) {
            textConverted += String.valueOf(text.charAt(0)).toUpperCase();
            for (int i = 1; i < text.length(); i++) {
                textConverted += String.valueOf(text.charAt(i)).toLowerCase();
            }
        }
        return textConverted;
    }

    public String fixNameArray(String[] array) {
        String nuevoNombre = "";
        for (int i = 0; i < array.length; i++) {
            if (array[i].length() > 0) {
                String tmp = transformTextToCamelNotation(array[i]);
                tmp = tmp.trim();
                if (i == array.length - 1) {
                    nuevoNombre += tmp;
                } else {
                    nuevoNombre += (tmp + " ");
                }
            }
        }
        return nuevoNombre;
    }

    private String fixName(String name) {
        StringBuilder retVal = new StringBuilder();

        for (String word : name.split(" ")) {
            retVal.append(
                    Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(' ');
        }

        if (retVal.charAt(retVal.length() - 1) == ' ') {
            retVal.deleteCharAt(retVal.length() - 1);
        }

        return retVal.toString();
    }

    
    public UsuarioVO convertToUsuarioVo(Usuario u) {
        UsuarioVO vo = new UsuarioVO();
        vo.setId(u.getId());
        vo.setNombre(u.getNombre());
        vo.setClave(u.getClave());
        vo.setPuesto(this.apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(u.getId(), u.getApCampo().getId()));
        vo.setMail(u.getEmail());
        vo.setDestinatarios(u.getDestinatarios());
        vo.setRfc(u.getRfc());
        vo.setTelefono(u.getTelefono());
        vo.setExtension(u.getExtension());
        vo.setCelular(u.getCelular());
        vo.setSexo(u.getSexo());
        vo.setIdCampo(u.getApCampo().getId());
        vo.setCampo(u.getApCampo().getNombre());
        vo.setActivo(u.isActivo());
        vo.setPregunta(u.getPreguntaSecreta());
        vo.setRespuesta(u.getRespuestaPreguntaSecreta());
        vo.setFechaIngreso(u.getFechaIngreso());

        if (u.getSgOficina() != null) {
            vo.setOficina(u.getSgOficina().getNombre());
            vo.setIdOficina(u.getSgOficina().getId());
        }
        if (u.getGerencia() == null) {
            vo.setIdGerencia(-1);
        } else {
            vo.setIdGerencia(u.getGerencia().getId());
            vo.setGerencia(u.getGerencia().getNombre());
        }
        if (u.getSgEmpresa() != null) {
            vo.setIdNomina(u.getSgEmpresa().getId());
        }

        vo.setAdministraTI(false);
        //buscar si es un usuario que administra TI
        List<UsuarioTipoVo> usuarioList = this.siUsuarioTipoRemote.getListUser(19, 1);
        if (usuarioList != null && !usuarioList.isEmpty()) {
            for (UsuarioTipoVo utvo : usuarioList) {
                if (utvo.getIdUser().equals(u.getId())) {
                    vo.setAdministraTI(true);
                    break;
                }
            }
        }
        vo.setUsuarioInSessionGerente(this.gerenciaRemote.isUsuarioResponsableForAnyGerencia(-1, u.getId(), false));
        vo.setLiberaUsuarios(this.rhTipoGerenciaRemote.isLiberador(u.getId()));
        return vo;
    }

    
    public Usuario buscarPorNombre(Object nombre) {
        try {
            return (Usuario) em.createQuery(
                    "SELECT u FROM Usuario u WHERE "
                    + "u.eliminado = :eliminado and u.activo = :activo and u.nombre = :nombre ")
                    .setParameter("nombre", nombre)
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .setParameter("activo", Constantes.BOOLEAN_TRUE)
                    .getResultList()
                    .get(0);
        } catch (Exception e) {
            log.warn(Constantes.VACIO, e);
            return null;
        }
    }

    
    public List<Usuario> getAnalistas() {
        return em.createQuery(
                "SELECT u FROM Usuario u WHERE u.compra = :compra")
                .setParameter("compra", Constantes.BOOLEAN_TRUE)
                .getResultList();
    }

    
    public List<UsuarioVO> getApruebanOrden(int campo) {
        List<UsuarioVO> lo = new ArrayList<>();

        String sql = CONSULTA
                + "         inner join OC_FLUJO fl on fl.EJECUTA = u.ID  "
                + "  where u.interno = true"
                + "  and u.ELIMINADO = 'False' "
                + "  AND u.activo = true"
                + "  and fl.ELIMINADO = 'False' "
                + "  and fl.AP_CAMPO = ?"
                + "  and fl.ACCION = 'AP' ";

        List<Object[]> result
                = em.createNativeQuery(sql)
                        .setParameter(1, campo)
                        .getResultList();

        log.info("Rrecupera analista procura : {}", sql);
        if (result != null) {
            for (Object[] object : result) {
                lo.add(castUsuario(object));
            }
        }
        return lo;
    }

    /*
     * Recupera los aprobadores de OC/S de acuerdo al capo en seleccionado
     * @campo
     */
    
    public List<UsuarioVO> getApruebanOrden() {
        List<UsuarioVO> lo = new ArrayList<>();
        String sql = CONSULTA
                + "     inner join OC_FLUJO fl on fl.EJECUTA = u.ID  "
                + " where u.interno = true"
                + "  and  u.ELIMINADO = 'False' "
                + "  AND u.activo = true"
                + " and fl.ELIMINADO = 'False' "
                + " and fl.ACCION = 'AP' ";

        List<Object[]> result = em.createNativeQuery(sql).getResultList();
        log.info("Rrecupera analista procura : {}", sql);
        if (result != null) {
            for (Object[] object : result) {
                lo.add(castUsuario(object));
            }
        }
        return lo;
    }

    //
    
    public boolean enviarClave(Usuario usuario) {
        return notificacionSistemaRemote.enviarClave(
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getClave(),
                usuario.getId()
        );
    }

    
    public List<Usuario> getActivos() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.activo = :activo")
                .setParameter("activo", Constantes.BOOLEAN_TRUE)
                .getResultList();
    }

//
    
    public boolean guardarNuevoUsuario(String sesion, UsuarioVO usuarioVO, int idGerencia) {
        boolean v = false;
        try {
            Usuario usuario = new Usuario();
            usuario.setId(usuarioVO.getId().toUpperCase());
            usuario.setNombre(fixName(usuarioVO.getNombre()));
            usuario.setApCampo(new ApCampo(usuarioVO.getIdCampo()));
            usuario.setClave(usuarioVO.getClave());

            usuario.setEmail(usuarioVO.getMail());
            usuario.setDestinatarios(usuarioVO.getMail());
            //
            usuario.setTelefono(usuarioVO.getTelefono());
            usuario.setExtension(usuarioVO.getExtension());
            usuario.setSexo(usuarioVO.getSexo());
            usuario.setCelular(usuarioVO.getCelular());
            usuario.setFechanacimiento(usuarioVO.getFechaNacimiento());
            usuario.setRfc(usuarioVO.getRfc());
            usuario.setPreguntaSecreta(usuarioVO.getPregunta());
            usuario.setRespuestaPreguntaSecreta(usuarioVO.getRespuesta());
            usuario.setFoto("/resources/imagenes/usuarios/usuario.png");
            usuario.setActivo(Constantes.BOOLEAN_TRUE);
            if (idGerencia > Constantes.CERO) {
                usuario.setGerencia(new  Gerencia(idGerencia));
            }
            usuario.setGenero(find(sesion));
            usuario.setFechaGenero(new Date());
            usuario.setHoraGenero(new Date());
            usuario.setEliminado(Constantes.NO_ELIMINADO);
            usuario.setInterno(usuarioVO.isInterno());
            this.create(usuario);

            //Guardar el usuario en campo-- usuario -- puesto
            if (usuarioVO.isInterno()) {
                apCampoUsuarioRhPuestoRemote.save(
                        sesion,
                        usuarioVO.getIdCampo(),
                        usuario.getId(),
                        usuarioVO.getIdPuesto(),
                        idGerencia
                );

            }

            v = true;
        } catch (Exception e) {
            LOGGER.fatal(this, "Guardar usuario : : : : : : " + e.getMessage(), e);
        }

        return v;
    }

    
//    public boolean modificarUsuario(String idUser, String nombre, String correo, String destinatarios, String telefono,
//	    String extension, String sexo, String celular, boolean adminContrato, boolean adminSGL,
//	    boolean responsableSGL, boolean seguridad, boolean asistenteDireccion, int idGerencia, boolean adminRH, int idCampo) {

    public boolean modificarUsuario(UsuarioVO usuarioVO, int idGerencia, int idCampo) {
        boolean v = false;
        try {

            Usuario usuario = this.find(usuarioVO.getId());
            usuario.setApCampo(apCampoRemote.find(idCampo));
            usuario.setNombre(fixName(usuarioVO.getNombre()));
            usuario.setGerencia(gerenciaRemote.find(idGerencia));
            usuario.setEmail(usuarioVO.getMail());
            usuario.setUsuarioDirectorio(usuarioVO.getUsuarioDirectorio());
            usuario.setDestinatarios(usuarioVO.getDestinatarios());
            usuario.setTelefono(usuarioVO.getTelefono());
            usuario.setExtension(usuarioVO.getExtension());
            usuario.setSexo(usuarioVO.getSexo());
            usuario.setCelular(usuarioVO.getCelular());
            edit(usuario);
            v = true;
        } catch (Exception e) {
            log.warn("Modificando usuario {}", usuarioVO.getId(), e);
        }

        return v;
    }

    
    public void eiminarUsuario(String sesion, String idUser) {
        try {
            Usuario usuario = find(idUser);
            usuario.setClave(encriptar(String.valueOf(generaClave())));
            usuario.setActivo(Constantes.BOOLEAN_FALSE);
            usuario.setEliminado(Constantes.ELIMINADO);
            usuario.setPreguntaSecreta(null);
            usuario.setRespuestaPreguntaSecreta(null);
            usuario.setModifico(find(sesion));
            usuario.setFechaModifico(new Date());
            usuario.setHoraModifico(new Date());
            edit(usuario);
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.fatal(ex);
        }
    }

    
    public void activarUsuario(String sesion, UsuarioVO usuarioVo) {
        Usuario usuario = find(usuarioVo.getId());
        usuario.setActivo(Constantes.BOOLEAN_TRUE);
        usuario.setEliminado(Constantes.NO_ELIMINADO);
        usuario.setModifico(find(sesion));
        usuario.setFechaModifico(new Date());
        usuario.setHoraModifico(new Date());
        this.edit(usuario);
    }

    /**
     * Creo: NLopez
     *
     * @param usuario
     * @param c
     * @param confirmarPasswor
     * @return
     */
    
    public List<RolVO> taerRoles(String usuario) {
        List<RolVO> lo = new ArrayList<RolVO>();

        String sql = "select r.id, r.nombre,"
                + " (select count(o.id) from si_rel_rol_opcion o where o.si_rol = r.id and o.eliminado ='False') as opciones, "
                + " m.nombre, "
                + " case when  (select count(r2.id) from SI_USUARIO_ROL ur, SI_ROL r2 where ur.USUARIO = ?"
                + " and ur.SI_ROL = r.ID) =0 then 'False'  "
                + " when  (select count(r2.id) from SI_USUARIO_ROL ur, SI_ROL r2 where ur.USUARIO = ?"
                + " and ur.SI_ROL = r.ID) >0 then true end "
                + " from si_rol r, si_modulo m  "
                + " where r.si_modulo = m.id ";

        List<Object[]> result
                = em.createNativeQuery(sql)
                        .setParameter(1, usuario)
                        .setParameter(2, usuario)
                        .getResultList();

        if (result != null) {
            for (Object[] object : result) {
                lo.add(castRoles(object));
            }
        }
        return lo;

    }

    /**
     * Creo: NLopez
     *
     * @param usuario
     * @param c
     * @param confirmarPasswor
     * @return
     */
    private RolVO castRoles(Object[] objects) {
        RolVO v = new RolVO();
        v.setId((Integer) objects[0]);
        v.setNombre((String) objects[1]);
        v.setOpciones((Integer) objects[2]);
        v.setModulo((String) objects[3]);
        v.setAsignado((String) objects[4]);
        return v;
    }

    
    public boolean cambioContrasenia(String usuario, String c, String confirmarPassword) {
        boolean v;
        Usuario u = find(usuario);

        v = this.notificacionSistemaRemote.enviarClaveIhsa(u.getNombre(), u.getEmail(), confirmarPassword);

        if (v) {
            u.setClave(c);
            edit(u);
            //v = true;
        }
        return v;
    }

    
    public boolean modificaUsuarioDatosConClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular, String nuevaClave) {
        boolean v;
        Usuario usuario = find(idUser);
        usuario.setNombre(fixName(nombre));
        usuario.setEmail(correo);
        usuario.setDestinatarios(destinatarios);
        usuario.setRfc(rfc);
        usuario.setTelefono(telefono);
        usuario.setExtension(ext);
        usuario.setCelular(celular);
        usuario.setModifico(usuario);
        usuario.setFechaModifico(new Date());
        usuario.setHoraModifico(new Date());

        v = this.notificacionSistemaRemote.enviarCorreoCambioClaveIhsa(usuario.getNombre(), usuario.getEmail());
        if (v) {
            //            this.siLogDocumentoRemote.guardarLog(9, usuario.getId(), 1, "Cambio de clave");
            usuario.setClave(nuevaClave);
            this.edit(usuario);
        }
        return v;
    }

    
    public void cambioPassTodosUsuarios() throws NoSuchAlgorithmException {
        List<Usuario> listaUsers = this.findAll();
        //List<Usuario> listaUsers = em.createQuery("SELECT u FROM Usuario u WHERE u.id = :p OR u.id = :id ").setParameter("p", "PRUEBA").setParameter("id", "MLUIS").getResultList();
        try {
            for (Usuario usuario : listaUsers) {
                String clave = usuario.getClave();

                //FIXME : por seguridad no debiera mandarse esto al archivo de log
                LOGGER.debug(this, "Clave: " + clave);
                usuario.setClave(encriptar(clave));
                this.edit(usuario);

                LOGGER.debug(this, "Clave despues" + usuario.getClave());
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(this, e);
        }

    }

    
    public String encriptar(String text) throws NoSuchAlgorithmException {
//	LOGGER.info(this, ": " + text);

        String retVal = text;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] b = md.digest(text.getBytes());
            int size = b.length;
            StringBuilder h = new StringBuilder(size);
            for (int i = 0; i < size; i++) {
                int u = b[i] & 255;
                if (u < 16) {
                    h.append(Integer.toHexString(u));
                } else {
                    h.append(Integer.toHexString(u));
                }
            }
            //clave encriptada
            retVal = h.toString();
        } catch (Exception e) {
            log.info("", e);
        }

        return retVal;
    }

    
    public boolean reinicioClave(String sesion, String idUser) {
        boolean v;
        Usuario usuario = find(idUser);
        int clave = generaClave();
        v = notificacionSistemaRemote.enviarReinicioClave(
                usuario.getNombre(),
                usuario.getEmail(),
                String.valueOf(clave),
                usuario.getId()
        );

        if (v) {
            try {
                usuario.setClave(encriptar(String.valueOf(clave)));
                usuario.setModifico(find(sesion));
                usuario.setFechaModifico(new Date());
                usuario.setHoraModifico(new Date());
                edit(usuario);
            } catch (NoSuchAlgorithmException ex) {
                log.error("", ex);
            }
        }
        return v;
    }

    private int generaClave() {
        int valorEntero = (int) Math.floor(Math.random() * (1000 - 10000 + 1) + 10000);
        return valorEntero;
    }

    
    public void modificarUsuario(Usuario usuario, int idGerencia) {
        usuario.setGerencia(gerenciaRemote.find(idGerencia));
        edit(usuario);
    }

    
    public boolean isAnalistaSGL(String idUsuario) throws Exception {
        List<SgOficinaAnalista> oficinaAnalistaList
                = sgOficinaAnalistaRemote.getOficinasByAnalistaAndStatus(
                        super.find(idUsuario),
                        Constantes.NO_ELIMINADO
                );

        return !oficinaAnalistaList.isEmpty();
    }

    
    public boolean isGerente(int idApCampo, String idUsuario) {
        LOGGER.info(this, "UsuarioImpl.isGerente()");
        return this.gerenciaRemote.isUsuarioResponsableForAnyGerencia(idApCampo, idUsuario, false);
    }

    
    public void modificaUsuarioDatosSinClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular) {
        Usuario usuario = find(idUser);
        usuario.setNombre(fixName(nombre));
        usuario.setEmail(correo);
        usuario.setDestinatarios(destinatarios);
        usuario.setRfc(rfc);
        usuario.setTelefono(telefono);
        usuario.setExtension(ext);
        usuario.setCelular(celular);
        usuario.setModifico(usuario);
        usuario.setFechaModifico(new Date());
        usuario.setHoraModifico(new Date());
        edit(usuario);
    }

    
    public UsuarioVO findById(Object idUsuario) {
        UsuarioVO retVal = null;

        try {
            clearQuery();
            UsuarioVO vo = new UsuarioVO();
            query.append(CONSULTA);
            query.append("  where u.interno =  true");
            query.append(" and u.id = ?");
            query.append(" AND u.activo = true");
            //
            Object[] result = (Object[]) em.createNativeQuery(query.toString())
                    .setParameter(1, idUsuario)
                    .getSingleResult();

            if (result != null) {
                vo = castUsuario(result);
            }

            retVal = vo;
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion usuario: " + e.getMessage(), e);
        }

        return retVal;
    }

    /**
     *
     * @param usuarioIds
     * @return
     */
    
    public List<UsuarioVO> findUsuariosById(List<String> usuarioIds) {

        List<UsuarioVO> resultado;

        if (UtilSia.isNullOrEmpty(usuarioIds)) {
            resultado = new ArrayList<>();
        } else {
            // preparar query en función de los parámetros recibidos
            StringBuilder sb = new StringBuilder();
            sb.append(CONSULTA);

            sb.append(" where u.interno = true and u.id in (");
            sb.append(UtilSia.toCommaSeparatedString(usuarioIds, true));
            sb.append(" ) ");

            Query q = em.createNativeQuery(sb.toString());

            List<Object[]> resultadoConsulta = q.getResultList();

            resultado = castVo(resultadoConsulta);

        }

        return resultado;

    }

    /**
     *
     * @param resultadoConsulta
     * @return
     */
    private List<UsuarioVO> castVo(List<Object[]> resultadoConsulta) {
        List<UsuarioVO> resultado = new ArrayList<>();

        for (Object[] valor : resultadoConsulta) {

            UsuarioVO vo = castUsuario(valor);

            resultado.add(vo);
        }
        return resultado;
    }

    /**
     * Cast para consulta base.
     *
     * @param objects
     * @return
     */
    private UsuarioVO castUsuario(Object[] objects) {
        UsuarioVO v = null;
        try {
            v = new UsuarioVO();
            v.setId((String) objects[0]);
            v.setNombre((String) objects[1]);
            v.setClave((String) objects[2]);
            v.setMail((String) objects[3]);
            v.setDestinatarios((String) objects[4]);
            v.setTelefono((String) objects[5]);
            v.setExtension((String) objects[6]);
            v.setSexo((String) objects[7]);
            v.setCelular((String) objects[8]);
            v.setFechaNacimiento((Date) objects[9]);
            v.setRfc((String) objects[10]);
            v.setRfcEmpresa((String) objects[11]);
            v.setFoto((String) objects[12]);
            v.setPregunta((String) objects[13]);
            v.setRespuesta((String) objects[14]);
            v.setActivo((Boolean) objects[15]);
            v.setGenero((String) objects[16]);

            CampoUsuarioPuestoVo acmCampoUsuarioPuestoVo = 
                    apCampoUsuarioRhPuestoRemote.traerPuestoPorUsuarioCampo(
                            (String) objects[0], 
                            (Integer) objects[20]
                    );
            //
            if (acmCampoUsuarioPuestoVo != null) {
                v.setPuesto(acmCampoUsuarioPuestoVo.getPuesto());
                v.setIdPuesto(acmCampoUsuarioPuestoVo.getIdPuesto());
            } else {
                v.setPuesto("");
                v.setIdPuesto(0);
            }

            v.setCampo((String) objects[17]);
            v.setGerencia((String) objects[18]);
            //
            v.setIdGerencia(objects[19] != null ? (Integer) objects[19] : 0);
            v.setIdCampo((Integer) objects[20]);
            //
            v.setIdOficina(objects[21] != null ? (Integer) objects[21] : 0);

        } catch (Exception e) {
            LOGGER.fatal(this, "Error al castear usuario: " + e.getMessage(), e);
        }
        return v;
    }

    
    public void aprobarOrdenCompra(String sesion, String idUser, int campo, String accion) {
        OcFlujo flujo = ocFlujoRemote.getByUsrActionCampo(accion, campo, idUser);
        if (flujo != null && flujo.getId() > 0) {
            flujo.setEliminado(Constantes.BOOLEAN_FALSE);
            flujo.setModifico(find(sesion));
            flujo.setFechaModifico(new Date());
            flujo.setHoraModifico(new Date());
            ocFlujoRemote.edit(flujo);
        } else {
            flujo = new OcFlujo();
            flujo.setAccion(accion);
            Usuario usuario = find(idUser);
            flujo.setEjecuta(usuario);
            flujo.setApCampo(apCampoRemote.find(campo));
            flujo.setEliminado(Constantes.BOOLEAN_FALSE);
            flujo.setGenero(find(sesion));
            flujo.setFechaGenero(new Date());
            flujo.setHoraGenero(new Date());
            ocFlujoRemote.create(flujo);
        }

        //Log
    }

    
    public void quitarUsuarioApruebaOrdenCompra(String sesion, String idUser, int campo, String accion) {
        OcFlujo flujo = ocFlujoRemote.getByUsrActionCampo(accion, campo, idUser);
        if (flujo != null && flujo.getId() > 0) {
            flujo.setEliminado(Constantes.BOOLEAN_TRUE);
            flujo.setModifico(find(sesion));
            flujo.setFechaModifico(new Date());
            flujo.setHoraModifico(new Date());
            ocFlujoRemote.edit(flujo);
        }
        //Log
    }

    
    public void cambiarCampoUsuario(String idUser, String idUserModifico, int idCampo) {
        int idGerencia = apCampoUsuarioRhPuestoRemote.traerGerenciaUsuarioCampo(idUser, idCampo);        
        Usuario usuario = find(idUser);
        usuario.setApCampo(new ApCampo(idCampo));                
        usuario.setModifico(new Usuario(idUserModifico));
        usuario.setFechaModifico(new Date());
        usuario.setHoraModifico(new Date());
        if(idGerencia > 0){
            usuario.setGerencia(new Gerencia(idGerencia));
        }
        edit(usuario);
        //Log
    }

    
    public List<Usuario> findAll(int idApCampo, String orderByField, boolean sortAscending, Boolean activo, boolean eliminado) {
        LOGGER.info(this, "UsuarioImpl.findAll()");

        List<Usuario> list = null;

        StringBuffer sql = new StringBuffer("SELECT u FROM Usuario u WHERE u.eliminado = :eliminado");

        if (idApCampo > 0) {
            sql.append(" AND u.apCampo.id = :idApCampo");
        }
        if (activo != null) {
            sql.append(" AND u.activo = :activo");
        }

        if (orderByField != null && !orderByField.isEmpty()) {
            sql.append(" ORDER BY u.")
                    .append(orderByField).append(' ')
                    .append((sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
        }

        Query q = em.createQuery(sql.toString());

        //Asignando parámetros
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

        if (idApCampo > 0) {
            q.setParameter("idApCampo", idApCampo);
        }
        if (activo != null) {
            q.setParameter("activo", (activo ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE));
        }

        LOGGER.info(this, "query Usuarios: " + q.toString());

        list = q.getResultList();

        LOGGER.info(this, "Se encontraron " + ((list == null || list.isEmpty()) ? null : list.size()) + " Usuarios filtrados por idApCampo: " + idApCampo + " activo:" + activo + " eliminado: " + eliminado);

        return list;
    }

    
    public List<UsuarioVO> findAll(int idApCampo, int idGerencia, Boolean activo, String orderByField, boolean sortAscending, boolean eliminado) {
        clearQuery();

        StringBuffer sql = new StringBuffer();

        sql.append("SELECT u.ID, " //0
                + "u.NOMBRE " //1
                + "FROM USUARIO u ");
        sql.append("WHERE u.ELIMINADO='").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");

        if (activo != null) {
            sql.append("AND u.ACTIVO='").append(activo ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("' ");
        }
        if (idApCampo > 0) {
            sql.append("AND u.AP_CAMPO=").append(idApCampo).append(' ');
        }
        if (idGerencia > 0) {
            sql.append("AND u.GERENCIA=").append(idGerencia).append(' ');
        }
        if (orderByField != null && !orderByField.isEmpty()) {
            sql.append("ORDER BY u.")
                    .append(orderByField).append(' ')
                    .append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        LOGGER.info(this, sql.toString());

        List<Object[]> result = em.createNativeQuery(sql.toString()).getResultList();
        List<UsuarioVO> list = new ArrayList<>();
        UsuarioVO vo = null;

        for (Object[] objects : result) {
            vo = new UsuarioVO();
            vo.setId((String) objects[0]);
            vo.setNombre((String) objects[1]);
            list.add(vo);
        }

        LOGGER.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " Usuario");

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public List<Usuario> traerUsuarioAsisteneDireccion() {

        List<Usuario> retVal = null;
        try {
            retVal = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.asistenteDireccion = :asis AND u.activo = :act")
                    .setParameter("asis", Constantes.BOOLEAN_TRUE)
                    .setParameter("act", Constantes.BOOLEAN_TRUE)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return retVal;
    }

    
    public boolean guardarUsuarioNuevoIngreso(String sesion, UsuarioVO usuarioVO, int idGerencia) {
        LOGGER.info(this, "guardarUsuarioNuevoIngreso " + usuarioVO.getId());
        boolean v = false;

        try {
            Usuario usuario = new Usuario();
            usuario.setId(usuarioVO.getId().toUpperCase());

            usuario.setNombre(fixName(usuarioVO.getNombre()));
            usuario.setApCampo(apCampoRemote.find(usuarioVO.getIdCampo()));
            //Profesionales
            usuario.setSgOficina(sgOficinaRemote.find(usuarioVO.getIdOficina()));
            usuario.setSgEmpresa(sgEmpresaRemote.find(usuarioVO.getIdNomina()));
            usuario.setGerencia(gerenciaRemote.find(usuarioVO.getIdGerencia()));
            usuario.setJefeDirecto(find(usuarioVO.getIdJefe()));
            usuario.setFechaIngreso(usuarioVO.getFechaIngreso());
            //
            usuario.setGafete(usuarioVO.getGafete().equals("si") ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            //
            usuario.setApCampo(apCampoRemote.find(usuarioVO.getIdCampo()));
            //
            usuario.setClave(usuarioVO.getClave());
            usuario.setRequiereCorreo(Constantes.BOOLEAN_FALSE);

            usuario.setEmail(usuarioVO.getMail());
            
            usuario.setUsuarioDirectorio(usuarioVO.getUsuarioDirectorio());
            
            usuario.setDestinatarios(usuarioVO.getMail());
            usuario.setTelefono(usuarioVO.getTelefono());
            usuario.setExtension(usuarioVO.getExtension());
            usuario.setSexo(usuarioVO.getSexo());
            usuario.setCelular(usuarioVO.getCelular());
            usuario.setFechanacimiento(usuarioVO.getFechaNacimiento());
            usuario.setRfc(usuarioVO.getRfc());
            usuario.setPreguntaSecreta(usuarioVO.getPregunta());
            usuario.setRespuestaPreguntaSecreta(usuarioVO.getRespuesta());
            usuario.setFoto("/resources/imagenes/usuarios/usuario.png");
            usuario.setActivo(Constantes.BOOLEAN_TRUE);
            usuario.setGerencia(gerenciaRemote.find(idGerencia));
            usuario.setGenero(find(sesion));
            usuario.setFechaGenero(new Date());
            usuario.setHoraGenero(new Date());
            usuario.setEliminado(Constantes.NO_ELIMINADO);
            usuario.setInterno(Constantes.BOOLEAN_TRUE);

            //Pone el rol general
            this.create(usuario);
            RolVO rolVO = new RolVO();
            rolVO.setId(Constantes.ROL_EMPLEADO_GENERAL);
            //
            siUsuarioRolRemote.guardar(rolVO, usuario.getId(), true, usuarioVO.getIdCampo());
            //Guardar el usuario en campo-- usuario -- puesto
            apCampoUsuarioRhPuestoRemote.save(sesion, usuarioVO.getIdCampo(), usuario.getId(), usuarioVO.getIdPuesto(), idGerencia);
            //
            sgUsuarioRolGerenciaRemote.guardarUsuarioRolGerencia(sesion, usuario.getNombre().trim(),
                    Constantes.ROL_EMPLEADO_GENERAL, idGerencia);

            v = true;

        } catch (Exception e) {
            LOGGER.error(e);
        }

        return v;
    }

    
    public boolean guardarMotivoBaja(String idUsuarioBaja, String motivo, String idUsuarioRealizo) {
        boolean retVal = false;

        try {
            Usuario usuario = find(idUsuarioBaja);
            if (usuario != null) {
                usuario.setMotivoBaja(motivo);
                usuario.setFechaBaja(new Date());
                usuario.setHoraBaja(new Date());
                edit(usuario);
                //Log
                retVal = true;
            }

        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion al guadar el motivo de baja del usuario " + e.getMessage(), e);
        }

        return retVal;
    }

    /**
     * Al guardar el correo electronico se debe activar el usuario.
     *
     * @param idUsuarioNuevoIngreso
     * @param nuevaDireccionCorreo
     * @param idUsuario
     * @return
     */
    
    public boolean guardarDireccionMailNuevoIngreso(String idUsuarioNuevoIngreso, String nuevaDireccionCorreo, String idUsuario) {
        LOGGER.info(this, "Asigno correo :" + idUsuario);
        boolean v = false;
        List<Usuario> usuarioRh = null;
        String correoPara = "";
        String correoCopia = "";
        try {
            Usuario usuarioEncontrado = find(idUsuarioNuevoIngreso);
            if (usuarioEncontrado != null) {
                usuarioEncontrado.setEmail(nuevaDireccionCorreo);
                usuarioEncontrado.setModifico(find(idUsuario));
                usuarioEncontrado.setFechaModifico(new Date());
                usuarioEncontrado.setHoraModifico(new Date());
                usuarioEncontrado.setRequiereCorreo(Constantes.BOOLEAN_FALSE);
                usuarioEncontrado.setDestinatarios(nuevaDireccionCorreo);
                //              
                edit(usuarioEncontrado);

                if ("PRUEBA".equals(idUsuario)) {
                    LOGGER.info(this, "es prueba");
                    v = notificacionUsuarioRemote.enviarNotificacionAsignacionCorreo(idUsuarioNuevoIngreso, idUsuario);
                } else {
                    v = notificacionUsuarioRemote.enviarNotificacionAsignacionCorreo(idUsuarioNuevoIngreso,
                            idUsuario);
                }
            }

        } catch (Exception e) {
            LOGGER.fatal(
                    this,
                    "Exepcion al modificar la direccion de correo de usuario de nuevo ingreso " + e.getMessage(),
                    e
            );
        }

        return v;
    }

    
    public boolean enviarSolicitudMaterial(String sesion, UsuarioVO usuarioVOAlta, List<EmpleadoMaterialVO> listaFilasSeleccionadas, int nuevoIngreso) {
        boolean v = false;
        List<EmpleadoMaterialVO> lista = new ArrayList<EmpleadoMaterialVO>();
        Set<Integer> set = new TreeSet<Integer>();
        String correoPara = Constantes.VACIO;
        String asunto;
        Usuario usuarioEncontrado;
//	boolean configuraCorreo = false;

        if (nuevoIngreso == Constantes.UNO) {
            asunto = "Nuevo ingreso -- " + usuarioVOAlta.getNombre();
        } else {
            asunto = "Candidato en Proceso de Contratación -- " + usuarioVOAlta.getNombre();
        }

        for (EmpleadoMaterialVO empleadoMaterialVO : listaFilasSeleccionadas) {
            set.add(empleadoMaterialVO.getIdGerencia());
            //set.add(54);
        }

        for (Integer integer : set) {

            for (EmpleadoMaterialVO empleadoMaterialVO : listaFilasSeleccionadas) {
                if (integer == empleadoMaterialVO.getIdGerencia()) {
                    lista.add(empleadoMaterialVO);
                }

            } // fin del empleado

            correoPara = correoPara(integer, usuarioVOAlta.getIdCampo());
            v
                    = notificacionUsuarioRemote.enviarSolicitudMateriar(
                            correoPara,
                            correoCopia(integer, find(sesion).getEmail()),
                            asunto,
                            usuarioVOAlta,
                            lista,
                            integer,
                            nuevoIngreso
                    );

            if (v) {//si envio correo de solicitud de material
                //modificar registro usuario
                if (usuarioVOAlta.isRequiereConfiguracionCorreo()) {
                    LOGGER.info(this, "Buscar al usuario " + usuarioVOAlta.getId());
                    usuarioEncontrado = find(usuarioVOAlta.getId());
                    if (usuarioEncontrado != null) {
                        usuarioEncontrado.setRequiereCorreo(Constantes.BOOLEAN_TRUE);
                        usuarioEncontrado.setFechaModifico(new Date());
                        usuarioEncontrado.setHoraModifico(new Date());
                        usuarioEncontrado.setModifico(find(sesion));
                        edit(usuarioEncontrado);
                        //LOG
                        LOGGER.info(this, "El usuario requiere de Configuracion de correo");
                    }
                }
            }
            correoPara = "";
            lista = new ArrayList<EmpleadoMaterialVO>();
        } //Fin de notificar a las gerencias
        if (v) {
            //NOtifica al jefe inmediato
            UsuarioResponsableGerenciaVo uvo
                    = gerenciaRemote.traerResponsablePorApCampoYGerencia(
                            Constantes.AP_CAMPO_DEFAULT,
                            usuarioVOAlta.getIdGerencia()
                    );// find(usuarioVOAlta.getIdJefe()).getEmail();

            if (uvo != null) {
                correoPara = uvo.getEmailUsuario();
            }

            v = notificacionUsuarioRemote.enviarNotificacionSiolicitud(
                    correoPara,
                    find(sesion).getEmail(),
                    asunto,
                    usuarioVOAlta,
                    listaFilasSeleccionadas,
                    nuevoIngreso
            );

            if (v) {
                //NOtifica a responsable de RRHH
                correoPara
                        = gerenciaRemote.traerResponsablePorApCampoYGerencia(
                                Constantes.AP_CAMPO_DEFAULT,
                                Constantes.GERENCIA_ID_RR_HH
                        ).getEmailUsuario();

                v = notificacionUsuarioRemote.enviarNotificacionSiolicitud(
                        correoPara,
                        Constantes.VACIO,
                        asunto,
                        usuarioVOAlta,
                        listaFilasSeleccionadas,
                        nuevoIngreso
                );
            }
        }
        return v;
    }

    /**
     * Modifico: NLopez 07/11/2013 El adquirir correo
     *
     * @param idGerencia
     * @param campo
     * @return
     */
    private String correoPara(int idGerencia, int campo) {
        String correo = Constantes.VACIO;

        UsuarioResponsableGerenciaVo u = gerenciaRemote.traerResponsablePorApCampoYGerencia(campo, idGerencia);//getResponsableByApCampoAndGerencia(campo, idGerencia, false);
        if (u != null) {
            correo = u.getEmailUsuario();
        }

        return correo;
    }

    
    public Usuario findUsuarioResponsableCapacitacion() {
        LOGGER.info(this, "UsuarioImpl.findUsuarioResponsableCapacitacion()");

        Usuario u = null;

        try {

            u = (Usuario) em
                    .createQuery("SELECT u FROM Usuario u WHERE u.responsableCapacitacion = :rcap AND u.activo = :activo")
                    .setParameter("rcap", Constantes.BOOLEAN_TRUE)
                    .setParameter("activo", Constantes.BOOLEAN_TRUE)
                    .getSingleResult();

        } catch (NoResultException nre) {
            LOGGER.fatal(this, nre.getMessage());
        } catch (NonUniqueResultException nure) {
            LOGGER.fatal(this, nure.getMessage());
        }

        return u;
    }

    
    public List<UsuarioVO> usuarioActio(int idGerencia) {
        LOGGER.info(this, "Aca dentro de activo");
        List<UsuarioVO> list = null;
        try {

            LOGGER.info(this, "Aca dentro de activo 2");

            LOGGER.info(this, "Aca dentro de activo 3");

            StringBuffer sql = new StringBuffer();

            sql.append("select  u.id, u.nombre, rp.id, rp.nombre, cap.id, g.ID, g.NOMBRE from Usuario u"
                    + " inner join AP_CAMPO_USUARIO_RH_PUESTO cap on cap.USUARIO=u.ID"
                    + " inner join  RH_PUESTO rp on rp.ID=cap.RH_PUESTO"
                    + " inner join GERENCIA g on g.id = u.GERENCIA ");

            sql.append(" where u.activo = '").append(Constantes.BOOLEAN_TRUE).append("'");
            sql.append(" and u.AP_CAMPO = cap.AP_CAMPO");
            sql.append(" and u.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            sql.append(" and cap.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("'");
            sql.append(" and rp.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("'");
            sql.append(" and g.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("'");

            if (idGerencia > 0) {
                sql.append(" AND u.GERENCIA=").append(idGerencia).append(" ");
            }

            sql.append(" order by u.nombre asc ");
            LOGGER.info(this, "Q; " + sql.toString());
            List<Object[]> lo = em.createNativeQuery(sql.toString()).getResultList();

            list = new ArrayList<>();

            for (Object[] objects : lo) {
                list.add(castUsuarioVO(objects));
            }

            LOGGER.info(this, "List: " + list.size());

        } catch (Exception e) {
            LOGGER.error(e);
        }

        return list;
    }

    /**
     *
     * Regresa la lista de usuarios con roles que pertenezcan al modulo indicado
     * y que contengan la opción proporcionada.
     *
     * @param moduloId
     * @param nombreOpcion
     * @return
     */
    
    public List<UsuarioVO> obtenerUsuariosPorModuloOpcion(int moduloId, String nombreOpcion) {

        String sql
                = "select  "
                + "USU.ID as usuario_id, "
                + "usu.NOMBRE as nombre_usuario, "
                + "usu.email as email "
                + "from si_opcion opc "
                + "inner join SI_REL_ROL_OPCION rol_opc on (rol_opc.SI_OPCION = opc.ID) "
                + "inner join si_rol rol on (rol_opc.SI_ROL = rol.ID) "
                + "inner join si_modulo mod on (opc.SI_MODULO = mod.ID) "
                + "inner join si_usuario_rol usu_rol on (usu_rol.SI_ROL = rol.id) "
                + "inner join usuario usu on (usu.ID = usu_rol.USUARIO) "
                + "where opc.NOMBRE = ? "
                + "and mod.id = ? "
                + "and rol_opc.ELIMINADO = 'False' "
                + "and rol.ELIMINADO = 'False' "
                + "and mod.ELIMINADO = 'False' "
                + "and usu_rol.ELIMINADO = 'False' "
                + "and usu.ELIMINADO = 'False' "
                + "order by mod.nombre, rol.nombre ";

        Query q = em.createNativeQuery(sql);

        q.setParameter(1, nombreOpcion);
        q.setParameter(2, moduloId);

        List<Object[]> resultado = q.getResultList();
        List<UsuarioVO> usuarios = new ArrayList<>();

        for (Object[] obj : resultado) {
            UsuarioVO vo = new UsuarioVO();

            vo.setId(String.valueOf(obj[0]));
            vo.setNombre(String.valueOf(obj[1]));
            vo.setMail(String.valueOf(obj[2]));

            usuarios.add(vo);
        }

        return usuarios;
    }

    /**
     *
     * @param moduloId
     * @param nombrePermiso
     * @return
     */
    
    public List<List<UsuarioVO>> obtenerUsuariosPorModuloPermiso(int moduloId, String nombrePermiso) {

        String sql = "select  "
                + "USU.ID as usuario_id, "
                + "usu.NOMBRE as nombre_usuario, "
                + "usu.email as email, "
                + "usu_rol.AP_CAMPO, "
                + "apc.NOMBRE "
                + "from si_permiso per "
                + "inner join SI_REL_ROL_PERMISO rol_per on (rol_per.SI_PERMISO = per.ID) "
                + "inner join si_rol rol on (rol_per.SI_ROL = rol.ID) "
                + "inner join si_modulo mod on (per.SI_MODULO = mod.ID) "
                + "inner join si_usuario_rol usu_rol on (usu_rol.SI_ROL = rol.id) "
                + "inner join usuario usu on (usu.ID = usu_rol.USUARIO) "
                + "INNER JOIN AP_CAMPO apc  on(apc.id=usu_rol.AP_CAMPO)"
                + "where per.NOMBRE = ? "
                + "and mod.id = ? "
                + "and rol_per.ELIMINADO = 'False' "
                + "and rol.ELIMINADO = 'False' "
                + "and mod.ELIMINADO = 'False' "
                + "and usu_rol.ELIMINADO = 'False' "
                + "and usu.ELIMINADO = 'False' "
                + "and usu.ACTIVO = true "
                + " order by mod.nombre, rol.nombre, usu_rol.AP_CAMPO ";

        Query q = em.createNativeQuery(sql);

        q.setParameter(1, nombrePermiso);
        q.setParameter(2, moduloId);
        // q.setParameter(3, Constantes.AP_CAMPO_NEJO); se quita para recuperar todos los campos

        List<Object[]> resultado = q.getResultList();
        List<UsuarioVO> usuarios = new ArrayList<>();
        List<List<UsuarioVO>> usuariosList = new ArrayList<List<UsuarioVO>>();
        int campoActual = 0;

        for (Object[] obj : resultado) {
            UsuarioVO vo = new UsuarioVO();

            vo.setId(String.valueOf(obj[0]));
            vo.setNombre(String.valueOf(obj[1]));
            vo.setMail(String.valueOf(obj[2]));
            vo.setIdCampo(Integer.valueOf(obj[3].toString()));
            vo.setCampo(String.valueOf(obj[4]));

            if (campoActual == 0) {
                campoActual = vo.getIdCampo();
                usuarios.add(vo);
            } else {
                if (campoActual == vo.getIdCampo()) {
                    usuarios.add(vo);
                } else {
                    campoActual = vo.getIdCampo();
                    usuariosList.add(usuarios);
                    usuarios = new ArrayList<>();
                    usuarios.add(vo);
                }
            }
        }
        if (usuarios != null && !usuarios.isEmpty()) {
            usuariosList.add(usuarios);
        }

        return usuariosList;

    }

    /**
     * Metodo para traer todos los usuarios con un rol 21/10/2013 Author: NLopez
     *
     * @param rol
     * @return List<UsuarioVO>
     */
    
    public List<UsuarioVO> getUsuariosByRol(Integer rol) {
        List<UsuarioVO> list = null;

        try {
            String sql
                    = "select  ur.USUARIO, '', 0, '',u.email, u.gerencia, ''  "
                    + " from SI_ROL r, SI_USUARIO_ROL ur, USUARIO u "
                    + " WHERE ur.SI_ROL = r.ID "
                    + " AND ur.IS_PRINCIPAL =true "
                    + " AND ur.usuario = u.id "
                    + "AND r.ID = ?";

            List<Object[]> lo
                    = em.createNativeQuery(sql)
                            .setParameter(1, rol)
                            .getResultList();

            list = new ArrayList<>();

            for (Object[] objects : lo) {
                list.add(castUsuarioVO(objects));
            }

            LOGGER.info(this, "List: " + list.size());
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return list;
    }

    /**
     * Obtiene los usuarios con el rol indicado independientemente si el rol es
     * principal.
     *
     * @param rolId
     * @return
     */
    
    public List<UsuarioVO> getUsuariosPorRol(Integer rolId) {

        List<UsuarioVO> list = new ArrayList();

        String sb
                = "select ur.USUARIO, '', 0, '', u.email, u.gerencia, ''  "
                + " from SI_ROL r, SI_USUARIO_ROL ur, USUARIO u "
                + "WHERE ur.SI_ROL = r.ID "
                + " AND ur.usuario = u.id "
                + " AND r.ID = ? ";

        Query q = em.createNativeQuery(sb);

        q.setParameter(1, rolId);

        List<Object[]> results = q.getResultList();

        for (Object[] objects : results) {
            list.add(castUsuarioVO(objects));
        }

        return list;
    }

    /**
     *
     * @param rolId
     * @param bloqueId
     * @return
     */
    
    public List<UsuarioVO> getUsuariosPorRolBloque(int rolId, int bloqueId) {

        List<UsuarioVO> list = new ArrayList();

        String sql = "select distinct "
                + "ur.USUARIO, '', 0, '', u.email, u.gerencia, '' "
                + "from  "
                + "SI_ROL r "
                + "inner join SI_USUARIO_ROL ur on (ur.SI_ROL = r.ID) "
                + "inner join USUARIO u on (ur.usuario = u.id ) "
                + "inner join AP_CAMPO_USUARIO_RH_PUESTO cup "
                + "on (cup.USUARIO = u.ID and u.ACTIVO = true and cup.ELIMINADO = 'False') "
                + "inner join AP_CAMPO bloq on (cup.AP_CAMPO = bloq.ID and bloq.ELIMINADO = 'False') "
                + "where 1=1 "
                + "and r.ID = ? "
                + "and bloq.id = ? ";

        Query q = em.createNativeQuery(sql);

        q.setParameter(1, rolId);
        q.setParameter(2, bloqueId);

        List<Object[]> results = q.getResultList();

        for (Object[] objects : results) {
            list.add(castUsuarioVO(objects));
        }

        return list;

    }

    /**
     *
     * @param rolCodigo
     * @param bloqueId
     * @return
     */
    
    public List<UsuarioVO> getUsuariosPorRolBloque(
            String rolCodigo, int bloqueId) {

        return getUsuariosPorRolBloque(rolCodigo, bloqueId, null, null, null);

    }

    /**
     *
     * @param rolCodigo
     * @param bloqueId
     * @param usuarioId
     * @param usuarioNombre
     * @param usuarioIdsExcluir
     * @return
     */
    
    public List<UsuarioVO> getUsuariosPorRolBloque(
            String rolCodigo,
            int bloqueId,
            String usuarioId,
            String usuarioNombre,
            List<String> usuarioIdsExcluir) {

        List<UsuarioVO> list = new ArrayList();

        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        sb.append("distinct ");
        sb.append("ur.USUARIO, ");
        sb.append("u.nombre, ");
        sb.append("0, ");
        sb.append("'', ");
        sb.append("u.email, ");
        sb.append("u.gerencia, ''");

        sb.append("from  ");
        sb.append("SI_ROL r ");
        sb.append("inner join SI_USUARIO_ROL ur on (ur.SI_ROL = r.ID) ");
        sb.append("inner join USUARIO u on (ur.usuario = u.id ) ");
        sb.append("inner join AP_CAMPO_USUARIO_RH_PUESTO cup ");
        sb.append("on (cup.USUARIO = u.ID and u.ACTIVO = true and cup.ELIMINADO = 'False') ");
        sb.append("inner join AP_CAMPO bloq on (cup.AP_CAMPO = bloq.ID and bloq.ELIMINADO = 'False') ");
        sb.append("where 1=1 ");
        sb.append("and r.codigo = ? ");

        if (!rolCodigo.equals("10-RUG")) {
            sb.append("and bloq.id = ? ");
        }

        if (!UtilSia.isNullOrBlank(usuarioId)) {
            sb.append("and u.id = ? ");
        }

        if (!UtilSia.isNullOrBlank(usuarioNombre)) {
            // TODO : revisar al migrar a PgSQL
            sb.append("and u.nombre COLLATE \"es_ES\" like ? ");
        }

        if (!UtilSia.isNullOrEmpty(usuarioIdsExcluir)) {
            sb.append("and u.id not in (");
            sb.append(UtilSia.toCommaSeparatedString(usuarioIdsExcluir, true));
            sb.append(")");
        }

        Query q = em.createNativeQuery(sb.toString());

        int i = 0;

        q.setParameter(++i, rolCodigo);
        q.setParameter(++i, bloqueId);

        if (!UtilSia.isNullOrBlank(usuarioId)) {
            q.setParameter(++i, usuarioId);
        }

        if (!UtilSia.isNullOrBlank(usuarioNombre)) {
            q.setParameter(++i, "%" + usuarioNombre + "%");
        }
        List<Object[]> results = q.getResultList();

        for (Object[] objects : results) {
            list.add(castUsuarioVO(objects));
        }

        return list;

    }

    private UsuarioVO castUsuarioVO(Object[] objects) {
        UsuarioVO vo = new UsuarioVO();
        try {
            vo.setId((String) objects[0]);
            vo.setNombre((String) objects[1]);
            vo.setIdPuesto((Integer) objects[2]);
            vo.setPuesto((String) objects[3]);
            vo.setIdGerencia((Integer) objects[5]);
            vo.setGerencia((String) objects[6]);
            if ((Integer) objects[2] == 0) {
                vo.setMail((String) objects[4]);
            } else {
                vo.setIdCampo((Integer) objects[4]);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al hacer el casting de usuario");
            LOGGER.fatal(this, e.getMessage());
        }
        return vo;
    }

    //FIXME : cambiara las literales numéricas por constantes
    private String correoCopia(int idGerencia, String mail) {
        StringBuilder correo = new StringBuilder(mail);
        //if ((idGerencia == 48)) {//Administracion  Se cambia por seguridad 48
        switch (idGerencia) {
            case 51: {
                //Administracion  Se cambia por RRHH 51
                List<UsuarioTipoVo> ut = siUsuarioTipoRemote.getListUser(Constantes.ID_TIPO_GENERAL_DIRECCION_GENERAL, Constantes.ID_OFICINA_TORRE_MARTEL);
                for (UsuarioTipoVo usuarioTipoVo : ut) {
                    correo.append(',').append(usuarioTipoVo.getCorreo());
                }
                break;
            }
            case 59: {
                //Comunicacion coorporativa
                List<UsuarioTipoVo> ut = siUsuarioTipoRemote.getListUser(21, Constantes.ID_OFICINA_TORRE_MARTEL);
                for (UsuarioTipoVo usuarioTipoVo : ut) {
                    correo.append(',').append(usuarioTipoVo.getCorreo());
                }
                break;
            }
            case 61: {
                //Servicios informaticos
                List<UsuarioTipoVo> ut = siUsuarioTipoRemote.getListUser(19, Constantes.ID_OFICINA_TORRE_MARTEL);
                for (UsuarioTipoVo usuarioTipoVo : ut) {
                    correo.append(',').append(usuarioTipoVo.getCorreo());
                }
                break;
            }
            case 54: {
                //Costos
                List<UsuarioTipoVo> ut = siUsuarioTipoRemote.getListUser(22, Constantes.ID_OFICINA_TORRE_MARTEL);
                for (UsuarioTipoVo usuarioTipoVo : ut) {
                    correo.append(',').append(usuarioTipoVo.getCorreo());
                }
                break;
            }
            case 48: {
                //SubDireccion administrativa
                List<UsuarioTipoVo> ut = siUsuarioTipoRemote.getListUser(24, Constantes.ID_OFICINA_TORRE_MARTEL);
                for (UsuarioTipoVo usuarioTipoVo : ut) {
                    correo.append(',').append(usuarioTipoVo.getCorreo());
                }
                break;
            }
            case 33: {
                //Servicios Generales
                List<UsuarioTipoVo> ut = siUsuarioTipoRemote.getListUser(27, Constantes.ID_OFICINA_TORRE_MARTEL);
                for (UsuarioTipoVo usuarioTipoVo : ut) {
                    correo.append(',').append(usuarioTipoVo.getCorreo());
                }
                break;
            }
            default:
                break;
        }

        return correo.toString();
    }

    
    public boolean finalizarBaja(String idSesion, String idUsuario) {
        boolean retVal = false;

        try {
            Usuario usuario = find(idUsuario);
            usuario.setSeguridad(Constantes.BOOLEAN_FALSE);
            usuario.setActivo(Constantes.BOOLEAN_FALSE);
            usuario.setEliminado(Constantes.ELIMINADO);
            usuario.setClave(encriptar(String.valueOf(generaClave())));
            usuario.setModifico(find(idSesion));
            usuario.setFechaModifico(new Date());
            usuario.setHoraModifico(new Date());
            edit(usuario);

            //finaliza baja
            //Publica la noticia
            int idNoticia = coNoticiaUsuarioRemote.buscarIdNoticiaPorUsuario(idUsuario, 23);
            if (idNoticia > 0) {//Genera la noticia
                String commentario
                        = "El día "
                                .concat(Constantes.FMT_TextDateLarge.format(new Date()))
                                .concat(", terminó el proceso de separación laboral.");
                coNoticiaRemote.nuevoComentario(idNoticia, idSesion, commentario, false, false, usuario.getApCampo().getId(), Constantes.MODULO_ADMIN_SIA);
            }
            //Envia el correo
            StringBuilder cc = new StringBuilder();
            boolean primero = true;
            for (Usuario u : traerUsuariosAdministraRH()) {
                if (primero) {
                    primero = false;
                } else {
                    cc.append(',');
                }

                cc.append(u.getEmail());
            }

            notificacionUsuarioRemote.enviaCorreoNotificaUsuarioFinalizaBaja(
                    gerenciaRemote.getResponsableByApCampoAndGerencia(1, 54, false).getEmail(),
                    cc.toString(),
                    find("SIA").getEmail(),
                    idUsuario
            );

            retVal = true;
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error(ex);
        }

        return retVal;
    }

    
    public List<UsuarioVO> traerListaUsuariosSinCorreos() {
        List<UsuarioVO> lo;

        final String sql
                = "SELECT u.id, u.nombre, u.email AS mail"
                + "FROM usuario u "
                + "WHERE u.eliminado = 'False'"
                + " AND u.ACTIVO = true"
                + " AND u.requiere_correo = true"
                + "ORDER BY u.fecha_genero desc ";

        try {

            lo = dbCtx.fetch(sql).into(UsuarioVO.class);

        } catch (DataAccessException e) {
            lo = Collections.emptyList();
            LOGGER.error(this, "Excepcion al traer la lista de usuarios sin correo " + e.getMessage(), e);
        }

        return lo;
    }

    
    public List<Usuario> traerUsuariosAdministraRH() {
        List<Usuario> retVal = null;

        try {
            retVal = em.createQuery("SELECT u FROM Usuario u WHERE u.administraRh = :TRUE AND u.activo = :act")
                    .setParameter("TRUE", Constantes.BOOLEAN_TRUE)
                    .setParameter("act", Constantes.BOOLEAN_TRUE)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.error(this, "Excepcion al traer los usuarios que administran RH " + e.getMessage(), e);
        }

        return retVal;
    }

    
    public UsuarioVO findByName(String nombre) {
        UsuarioVO vo = null;

        try {
            String sql = CONSULTA
                    + " where u.interno = true"
                    + " and u.nombre = ?"
                    + " AND u.activo = true";

            Object[] result = (Object[]) em.createNativeQuery(sql)
                    .setParameter(1, nombre)
                    .getSingleResult();

            if (result != null) {
                vo = castUsuario(result);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion usuario: " + e.getMessage(), e);
        }

        return vo;
    }

    //TODO : revisar la lógica de esto, podría hacerse desde la base de datos?
    public String getMenosEstatusAprobacionByRol(Integer rol, Integer estatus) {
        List<UsuarioVO> usuarios = getUsuariosByRol(rol);
        Map<String, Integer>  usuariosMap = new HashMap<>();

        for (UsuarioVO usuarioVO : usuarios) {
//            usuarioVO.geti
            usuariosMap.put(
                    usuarioVO.getId(),
                    estatusAprobacion.getTotalEstatusAprobacionByUsuario(usuarioVO.getId(), estatus)
            );
        }

        //Ordenar Map por Valor
        HashMap<String, Object> mapResultado = new LinkedHashMap<>();
        List<String> misMapKeys = new ArrayList<>(usuariosMap.keySet());
        List<Integer> misMapValues = new ArrayList<>(usuariosMap.values());
        TreeSet<Integer> conjuntoOrdenado = new TreeSet<>(misMapValues);
        Object[] arrayOrdenado = conjuntoOrdenado.toArray();

        int size = arrayOrdenado.length;
        for (int i = 0; i < size; i++) {
            mapResultado.put(misMapKeys.get(misMapValues.indexOf(arrayOrdenado[i])), arrayOrdenado[i]);
        }
        return misMapKeys.get(misMapValues.indexOf(arrayOrdenado[0]));
    }

    
    public void agregaTelefonoUsuario(String idUsuario, String telefono, String idSesion) {
        Usuario usuario = find(idUsuario);
        usuario.setTelefono(telefono);
        usuario.setModifico(find(idSesion));
        usuario.setFechaModifico(new Date());
        usuario.setHoraModifico(new Date());
        edit(usuario);

        //
        
    }

    
    public List<UsuarioVO> traerRolPrincipalUsuarioRolModulo(int rol, int idModulo, String empresa) {

        List<UsuarioVO> lu = null;

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select u.id, u.nombre, ac.id, ac.nombre, u.email, u.AP_CAMPO "
                    + " from SI_USUARIO_ROL ur "
                    + " inner join USUARIO u on u.id =  ur.USUARIO and u.ELIMINADO = ? "
                    + " inner join AP_CAMPO ac on ac.id = ur.AP_CAMPO "
                    + " where ur.SI_ROL = ? "
                    + " and ur.IS_PRINCIPAL = ? "
                    + " and ur.SI_ROL in (select r.id from si_rol r where r.SI_MODULO = ?) "
                    + " and ur.eliminado = ? "
                    + " and ac.COMPANIA = ?");

            UtilLog4j.log.info(this, "Q: " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, rol)
                    .setParameter(3, Constantes.BOOLEAN_TRUE)
                    .setParameter(4, idModulo)
                    .setParameter(5, Constantes.NO_ELIMINADO)
                    .setParameter(6, empresa)
                    .getResultList();
            if (lo != null) {
                lu = new ArrayList<>();
                for (Object[] objects : lo) {
                    lu.add(castUsuarioParaRol(objects));
                }
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }

        return lu;
    }

    
    public List<UsuarioVO> traerListaRolPrincipalUsuarioRolModulo(int rol, int idModulo, int idCampo) {
        List<UsuarioVO> lu = null;

        final String sql = "SELECT DISTINCT ON(u.nombre) u.id, u.nombre, c.id AS id_campo, \n"
                + "  c.nombre AS campo, u.email AS mail \n"
                + "FROM usuario u  \n"
                + "      INNER JOIN si_usuario_rol ur ON ur.usuario = u.ID and ur.ELIMINADO = 'False' \n"
                + "      INNER JOIN ap_campo c ON ur.ap_campo = c.ID and c.ELIMINADO = 'False' AND c.id = ? \n"
                + "      INNER JOIN si_rol r ON ur.si_rol = r.id AND ur.si_rol = ? "
                + "         AND r.si_modulo = ? AND r.eliminado = 'False' \n"
                + "WHERE u.eliminado = 'False'\n"
                + "ORDER BY u.nombre ";

        try {

            lu = dbCtx.fetch(sql, idCampo, rol, idModulo).into(UsuarioVO.class);

        } catch (DataAccessException e) {
            lu = Collections.emptyList();

            LOGGER.error(
                    this,
                    "Ocurrio un error al recuperar el usuario a asignar la requision, no hay usuario con el rol o hay mas de uno {0} - {1}  - {2} - {3}",
                    new Object[]{sql, idCampo, rol, idModulo},
                    e
            );
        }

        return lu;
    }

    private UsuarioVO castUsuarioParaRol(Object[] objects) {
        UsuarioVO vo = new UsuarioVO();
        try {
            vo.setId((String) objects[0]);
            vo.setNombre((String) objects[1]);
            vo.setIdCampo((Integer) objects[2]);
            vo.setCampo((String) objects[3]);
            vo.setMail((String) objects[4]);
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al hacer el casting de usuairo puesto", e);
        }

        return vo;
    }

    
    public UsuarioVO traerResponsableGerencia(int idCampo, int gerencia, String empresa) {
        UsuarioVO retVal = null;

        try {

            String sql = "select u.id, u.nombre, u.email from USUARIO u  where u.ID = ("
                    + " select cg.RESPONSABLE from AP_CAMPO_GERENCIA cg where cg.GERENCIA = ?"
                    + " and cg.AP_CAMPO = ? "
                    + " and cg.eliminado = ? )";
            
            LOGGER.info(this, "Q: responsable gerencia: " + query.toString());
            
            Object[] obj = (Object[]) em.createNativeQuery(sql)
                    .setParameter(1, gerencia)
                    .setParameter(2, idCampo)
                    .setParameter(3, Constantes.BOOLEAN_FALSE)
                    .getSingleResult();

            if (obj != null) {
                retVal = castUsuarioParaUnRol(obj);
            }

        } catch (Exception e) {
            LOGGER.error(this, "Ocurrio un error al recuperar el responsable de la gerencia:  " + gerencia, e);
        }

        return retVal;
    }

    private UsuarioVO castUsuarioParaUnRol(Object[] objects) {
        UsuarioVO vo = new UsuarioVO();
        try {
            vo.setId((String) objects[0]);
            vo.setNombre((String) objects[1]);
            vo.setMail((String) objects[2]);
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al hacer el casting de usuairo puesto", e);
        }
        return vo;
    }

    
    public List<UsuarioVO> buscarResponsableGerencia(int idGerencia) {
        List<UsuarioVO> lu = null;

        try {
            String sql
                    = "select u.ID,u.NOMBRE, u.EMAIL, "
                    + "(select c.COMPANIA from AP_CAMPO c where c.ID = u.AP_CAMPO) from USUARIO u"
                    + " where u.ID in ("
                    + "     Select distinct(cg.RESPONSABLE) "
                    + "     from AP_CAMPO_GERENCIA cg, AP_CAMPO c "
                    + "     where c.COMPANIA in (select c.rfc from COMPANIA c )"
                    + " and cg.AP_CAMPO = c.id"
                    + " and cg.GERENCIA = ?)";

            log.info("Q: responsables gerencia 23: {}", sql);

            List<Object[]> obj = em.createNativeQuery(query.toString())
                    .setParameter(1, idGerencia)
                    .getResultList();

            if (obj != null) {
                lu = new ArrayList<>();
                for (Object[] objects : obj) {
                    lu.add(castUsuarioParaResponsableGerencia(objects));
                }
            }

        } catch (Exception e) {
            log.warn("Ocurrio un error al recuperar el responsable de la gerencia: {}",idGerencia, e);
        }

        return lu;
    }

    private UsuarioVO castUsuarioParaResponsableGerencia(Object[] objects) {
        UsuarioVO vo = new UsuarioVO();
        try {
            vo.setId((String) objects[0]);
            vo.setNombre((String) objects[1]);
            vo.setMail((String) objects[2]);
            vo.setRfcEmpresa((String) objects[3]);
        } catch (Exception e) {
            LOGGER.fatal(this, "Error al hacer el casting de gerncia usuario", e);
        }
        return vo;
    }

    
    public List<CompaniaVo> traerCompaniaPorUsuario(String idUsuario) {

        List<CompaniaVo> lc = null;
        String sql = "select cp.rfc, cp.nombre, cp.siglas from COMPANIA cp where cp.RFC in ("
                + "     select c.COMPANIA from AP_CAMPO c where c.ID in ("
                + "         select cup.AP_CAMPO from AP_CAMPO_USUARIO_RH_PUESTO cup "
                + "         where cup.USUARIO = ? "
                + "         and cup.ELIMINADO = ? ))";

        List<Object[]> lo = em.createNativeQuery(sql)
                .setParameter(1, idUsuario)
                .setParameter(2, Constantes.NO_ELIMINADO)
                .getResultList();
        //
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] objects : lo) {
                lc.add(castCompania(objects));
            }
        }
        return lc;
    }

    private CompaniaVo castCompania(Object[] objects) {
        CompaniaVo c = new CompaniaVo();
        c.setRfcCompania((String) objects[0]);
        c.setNombre((String) objects[1]);
        c.setSiglas((String) objects[2]);
        return c;

    }

    
    public List<UsuarioVO> traerUsuariosSolicitaRequision(int idCampo) {

        String sql = "SELECT distinct(u.id), " //0
                + " u.nombre," //1
                + " u.clave, "//2
                + " u.solicita, "//3
                + " u.revisa," // 4
                + " u.aprueba, " //5
                + " u.autoriza," // 6
                + " u.visto_bueno, " //7
                + " u.asigna," // 8
                + " u.compra, " //9
                + " u.email, " //10
                + " u.destinatarios," // 11
                + " u.telefono, " //12
                + " u.extension, " //13
                + " u.sexo, " //14
                + " u.celular," // 15
                + " u.fechanacimiento, " //16
                + " u.rfc," // 17
                + " u.foto, " //18
                + " u.publica," // 19
                + " u.notifica, " //20
                + " u.pregunta_secreta," // 21
                + " u.RESPUESTA_PREGUNTA_SECRETA, " //22
                + " u.activo," // 23
                + " u.administra_SGL, " //24
                + " u.responsable_SGL," // 25
                + " u.seguridad, " //26
                + " u.administra_SIA," // 27
                + " u.Administra_contrato, " //28
                + " u.genero," // 29
                + " u.asistente_direccion, " //30
                + " u.APROBAR_ORDEN," // 31
                + " (select c.nombre from ap_campo c where u.ap_campo = c.id), " // 34
                + " (select g.nombre from gerencia g where u.gerencia is not null and u.gerencia = g.id)," // 33
                + " u.gerencia,"
                + " u.ap_campo,"
                + " u.sg_oficina"
                + "  from USUARIO u"
                + " left join CADENAS_MANDO cm on u.ID = cm.USUARIO "
                + " where cm.AP_CAMPO = ?"
                + " and cm.ELIMINADO = ? "
                + " and u.ELIMINADO =  ? "
                + " and u.ACTIVO =  ? ";

        List<Object[]> result = em.createNativeQuery(sql)
                .setParameter(1, idCampo)
                .setParameter(2, Constantes.BOOLEAN_FALSE)
                .setParameter(3, Constantes.BOOLEAN_FALSE)
                .setParameter(4, Constantes.BOOLEAN_TRUE)
                .getResultList();

        List<UsuarioVO> lo = null;
        LOGGER.info(this, "Rrecupera solicitan req :   :    :   :  " + sql);

        if (result != null) {
            lo = new ArrayList<>();
            for (Object[] object : result) {
                lo.add(castUsuario(object));
            }
        }
        return lo;
    }

    //TODO : revisar si realmente se usa en algún lugar
    public String traerUsuarioActivoJson() {
        String retVal = null;

        try {
            Gson gson = new Gson();
            String sql
                    = "select u.id, u.nombre from usuario u where u.eliminado = ? and u.activo = ? and u.interno = true ";

            List<Object[]> lista = em.createNativeQuery(sql)
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.BOOLEAN_TRUE)
                    .getResultList();

            JsonArray a = new JsonArray();

            for (Object[] o : lista) {
                if (lista != null) {
                    JsonObject ob = new JsonObject();
                    ob.addProperty("value", o[0] != null ? (String) o[0] : "-");
                    ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                    a.add(ob);
                }
            }
            retVal = gson.toJson(a);

        } catch (Exception e) {
            log.warn("*** Al traer usuarios en formato JSON ...", e);
        }

        return retVal;
    }

    
    public List<Object[]> traerUsuarioActivosJson(int idGerencia) {
        List<Object[]> usuarios = null;
        String gerencia = "";

        if (idGerencia > 0) {
            gerencia = " AND u.gerencia = " + idGerencia;
        }
        try {
            String sql
                    = "select u.id, u.nombre, g.nombre from usuario u "
                    + " inner join GERENCIA g on g.id=u.GERENCIA"
                    + " where u.eliminado = ? and u.activo = ?"
                    + " and u.interno = true"
                    + gerencia
                    + " order by u.NOMBRE ";

            usuarios = em.createNativeQuery(sql)
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.BOOLEAN_TRUE)
                    .getResultList();

        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion los usuarios " + e.getMessage(), e);
        }

        return usuarios;
    }

    
    public void modificarDatosUsuario(String sesion, UsuarioVO usuarioVO, int idPuesto) {
        try {
            var usuario = find(usuarioVO.getId());

            var us = usuario.toString();
            usuario.setNombre(usuarioVO.getNombre());
            usuario.setEmail(usuarioVO.getMail());
            usuario.setDestinatarios(usuarioVO.getMail());
            usuario.setTelefono(usuarioVO.getTelefono());
            usuario.setCelular(usuarioVO.getCelular());
            usuario.setExtension(usuarioVO.getExtension());
            usuario.setSexo(usuarioVO.getSexo());
            usuario.setFechanacimiento(usuarioVO.getFechaNacimiento());
            usuario.setApCampo(apCampoRemote.find(usuarioVO.getIdCampo()));
            usuario.setSgOficina(sgOficinaRemote.find(usuarioVO.getIdOficina()));
            usuario.setGerencia(gerenciaRemote.find(usuarioVO.getIdGerencia()));
            usuario.setDestinatarios(usuarioVO.getDestinatarios());
            usuario.setFechaIngreso(usuarioVO.getFechaIngreso());
            usuario.setSgEmpresa(sgEmpresaRemote.find(usuarioVO.getIdNomina()));
            edit(usuario);
            apCampoUsuarioRhPuestoRemote.modificarUsuarioPuesto(
                    sesion,
                    usuarioVO.getIdCampo(),
                    usuarioVO.getId(),
                    idPuesto
            );
        } catch (Exception e) {
            LOGGER.error(this, "Ocurrio una excepcion al modificar el usuario  : : : : : " + e.getMessage(), e);
        }
    }

    
    public List<UsuarioVO> obtenerListaUsuarios() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery qry = criteriaBuilder.createQuery();
        Root<Usuario> usuario = qry.from(Usuario.class);
        qry.select(
                criteriaBuilder.construct(
                        UsuarioVO.class,
                        usuario.get("id"),
                        usuario.get("nombre"),
                        usuario.get("email")
                )
        );
        return getEntityManager().createQuery(qry).getResultList();
    }

    
    public List<UsuarioVO> obtenerListaUsuarios(Integer rol) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery qry = criteriaBuilder.createQuery();
        Root<Usuario> usuario = qry.from(Usuario.class);

        if (rol != null) {
            Join<Usuario, SiUsuarioRol> usuarioRolRoot = usuario.join("siUsuarioRolCollection", JoinType.INNER);
            Join<SiUsuarioRol, SiRol> rolRoot = usuarioRolRoot.join("siRol", JoinType.INNER);            
            Predicate predicateForROL = criteriaBuilder.equal(rolRoot.get("id"), rol);
            Predicate predicateForElIMINADO = criteriaBuilder.equal(usuario.get("eliminado"), false);            
            Predicate predicateForElIMINADORol = criteriaBuilder.equal(usuarioRolRoot.get("eliminado"), false);            
            Predicate finalPredicate = criteriaBuilder.and(predicateForElIMINADO, predicateForROL, predicateForElIMINADORol);            
            qry.where(finalPredicate);            
        }

        qry.select(
                criteriaBuilder.construct(
                        UsuarioVO.class,
                        usuario.get("id"),
                        usuario.get("nombre"),
                        usuario.get("email"),
                        criteriaBuilder.nullLiteral(Collection.class))
        );

        return getEntityManager().createQuery(qry).getResultList();
    }

    
    public Usuario login(String username, String passwordHash) {
        Usuario usuario = find(username);
        if (!usuario.getClave().equals(passwordHash)) {
            usuario = null;
        }
        return usuario;
    }

    /**
     *
     * @param cadena
     * @param numero
     * @return
     */
    public List<Object[]> buscarUsuarioLetras(String cadena, int numero) {
        StringBuilder sql = new StringBuilder();

        sql.append("select u.ID, u.NOMBRE, u.TELEFONO from USUARIO u")
                // TODO : revisar al migrar a PgSQL
                .append("where upper(u.NOMBRE COLLATE \"es_ES\") like upper('%").append(cadena).append("%')")
                .append("and u.ELIMINADO = false and u.interno = true ")
                .append("order by u.NOMBRE asc")
                //TODO : cambiar a LIMIT cuando se migre a PgSQL
                .append("rows ").append(numero);

        return em.createNativeQuery(sql.toString()).getResultList();
    }

    
    public String traerUsuarioActivoJsonByGerencia(int gerencia, int idOficina) {
        String retVal = null;

        try {
            Gson gson = new Gson();

            StringBuilder sql = new StringBuilder(
                    "select u.id, u.nombre,r.NOMBRE from usuario u "
                    + " inner join AP_CAMPO_USUARIO_RH_PUESTO a on a.USUARIO = u.ID "
                    + " inner join RH_PUESTO r on r.ID = a.RH_PUESTO"
                    + " inner join SG_LICENCIA l on l.USUARIO=u.ID"
                    + " inner join sg_oficina f on f.ap_campo = a.ap_campo"
                    + " where u.eliminado = ? " //1
                    + " and u.activo = ? " //2
                    + " and a.ELIMINADO = ? " //3
                    + " and f.id = ? " //4
                    + " and l.ELIMINADO= ? " //5
                    + " and l.VIGENTE= ? "
                    + " and u.interno = true "); //6
            if (gerencia > 0) {
                sql.append(" and u.gerencia = ? " //7
                        + " and a.GERENCIA = ? ");  //8
            } else {
                sql.append(" and u.gerencia NOT IN ( ?) " //7
                        + " and a.GERENCIA NOT IN ( ?) ");
            }

            sql.append(" order by u.nombre");

            List<Object[]> lista = em.createNativeQuery(sql.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.BOOLEAN_TRUE)
                    .setParameter(3, Constantes.NO_ELIMINADO)
                    .setParameter(4, idOficina)
                    .setParameter(5, Constantes.NO_ELIMINADO)
                    .setParameter(6, Constantes.BOOLEAN_TRUE)
                    .setParameter(7, gerencia)
                    .setParameter(8, gerencia)
                    .getResultList();

            JsonArray a = new JsonArray();

            for (Object[] o : lista) {
                if (lista != null) {
                    JsonObject ob = new JsonObject();
                    ob.addProperty("value", o[0] == null ? "-" : (String) o[0]);
                    ob.addProperty("label", o[1] == null ? "-" : (String) o[1]);
                    ob.addProperty("type", o[2] == null ? "-" : (String) o[2]);
                    a.add(ob);
                }
            }
            retVal = gson.toJson(a);

        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion los usuarios " + e.getMessage(), e);
        }

        return retVal;
    }

    
    public List<UsuarioVO> usuariosSinCurso(int idCampo) {
        
        List<UsuarioVO> usuarios;
        
        String sql = 
                "SELECT u.id, u.nombre, a.id AS id_campo, a.nombre AS campo, o.id AS id_oficina, o.nombre AS oficina\n"
                + "FROM USUARIO u\n"
                + "\tINNER JOIN ap_campo_usuario_rh_puesto acu ON acu.usuario = u.id AND acu.eliminado = FALSE AND u.id = acu.usuario\n"
                + "\tINNER JOIN ap_campo a ON a.id = acu.ap_campo AND a.eliminado= ?\n"
                + "\tINNER join sg_oficina o ON o.id = u.sg_oficina AND o.eliminado= ?  \n"
                + "WHERE u.eliminado = ? AND u.activo= ? AND u.interno=true AND a.id= ? \n"
                + "\tAND u.id NOT IN (SELECT usuario FROM sg_curso_manejo WHERE eliminado = ? )"
                + "ORDER BY o.id,u.gerencia,u.nombre";
        
        try {
            usuarios = 
                    dslCtx.fetch(sql, false, false, false, true, idCampo, false).into(UsuarioVO.class);
        } catch (DataAccessException e) {
            log.warn("*** Al recuperar usuarios sin curso ...", e);
            
            usuarios = Collections.emptyList();
        }
        

//        List<Object[]> lista = em.createNativeQuery(sql)
//                .setParameter(1, Constantes.FALSE)
//                .setParameter(2, Constantes.FALSE)
//                .setParameter(3, Constantes.FALSE)
//                .setParameter(4, Constantes.TRUE)
//                .setParameter(5, idCampo)
//                .setParameter(6, Constantes.FALSE)
//                .getResultList();

//        if (lista != null && !lista.isEmpty()) {
//            UsuarioVO user;
//            for (Object[] o : lista) {
//                user = new UsuarioVO();
//                user.setId((String) o[0]);
//                user.setNombre((String)o[1]);
//                user.setIdCampo((Integer)o[2]);
//                user.setCampo((String)o[3]);
//                user.setIdOficina((Integer)o[4]);
//                user.setOficina((String)o[5]);
//                
//                usuarios.add(user);
//            }
//        }


        return usuarios;
    }

    
    
    public Usuario getUsuarioForId(String usuarioId) {
        Usuario retVal = null;
        
        String sql = 
                "SELECT * \n" +
                "FROM usuario \n" +
                "WHERE id = ? OR usuario_directorio = ? \n" +
                "AND interno = true AND activo = true AND eliminado = false";
        
        try {
            Record recusuario = dslCtx.fetchOne(sql, usuarioId, usuarioId);
            
            if(recusuario != null) {
                retVal = recusuario.into(Usuario.class);
            }
        } catch (DataAccessException e) {
            LOGGER.error(this, "Usuario : {0}", new Object[]{usuarioId}, e);
        }
        
        return retVal;
    }
    
    
    public Usuario buscarPorId(String usuarioId) {
        Usuario retVal = null;
        
        String sql = 
                "SELECT *  FROM usuario \n" +
                " WHERE id = ? \n" +
                " AND eliminado = false";
        
        try {
            Record recUsuario = dslCtx.fetchOne(sql, usuarioId, usuarioId);
            
            if(recUsuario != null) {
                retVal = recUsuario.into(Usuario.class);
            }
        } catch (DataAccessException e) {
            LOGGER.error(this, "Usuario : {0}", new Object[]{usuarioId}, e);
        }
        
        return retVal;
    }
}
