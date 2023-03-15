package lector.servicios.catalogos.impl;

import com.google.common.base.Strings;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import lector.constantes.Constantes;
import lector.modelo.SiRol;
import lector.modelo.SiUsuarioRol;
import lector.modelo.Usuario;
import javax.ejb.Stateless;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.excepciones.LectorException;
import lector.notificaciones.sistema.impl.ServicioNotificacionSistemaImpl;
import lector.sistema.AbstractImpl;
import lector.util.UtilLog4j;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Stateless
@Slf4j
public class UsuarioImpl extends AbstractImpl<Usuario> {

    private static final String CONSULTA
            = "SELECT u.id, u.nombre, u.clave, u.email,  u.destinatarios, u.telefono, u.extension, "
            + "  u.sexo, u.celular, u.fechanacimiento, u.rfc, u.compania, u.foto, u.pregunta_secreta,"
            + "  u.respuesta_pregunta_secreta, u.activo, u.genero,"
            + "  (select c.nombre from ap_campo c where u.ap_campo = c.id), "
            + "  (select g.nombre from gerencia g where u.gerencia is not null and u.gerencia = g.id),"
            + "  u.gerencia, u.ap_campo,u.sg_oficina,"
            + "  u.fecha_ingreso,u.sg_empresa,"
            + "  (select o.nombre from sg_oficina o where o.id = u.sg_oficina)"
            + " FROM usuario u ";

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private ServicioNotificacionSistemaImpl notificacionSistemaRemote;

    public UsuarioImpl() {
        super(Usuario.class);
    }

    public UsuarioVO login(final String correo, final String pass) throws LectorException {

        if (Strings.isNullOrEmpty(correo) || Strings.isNullOrEmpty(pass)) {

            throw new LectorException("El usuario y la clave son requeridos.");

        }
        Usuario usuario = find(correo);

        if (usuario == null) {
            throw new LectorException("Usuario no encontrado.");

        }

        if (!usuario.getClave().equals(encriptar(pass))) {
            throw new LectorException("El usuario y/0 la clave son incorrectos.");
        }

        return castingToUsuarioVo(usuario);
    }

    private UsuarioVO castingToUsuarioVo(Usuario usuario) {

        return UsuarioVO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .cCuenta(usuario.getCCuenta().getId())
                .telefono(usuario.getTelefono())
                .sexo(usuario.getSexo())
                .cEstado(usuario.getCEstado() != null ? usuario.getCEstado().getId() : 0)
                .estado(usuario.getCEstado() != null ? usuario.getCEstado().getNombre():"")
                .estadoClave(usuario.getCEstado() != null ? usuario.getCEstado().getClave():0)
                .cMunicipio(usuario.getCMunicipio() != null ? usuario.getCMunicipio().getId() : 0)                
                .municipio(usuario.getCMunicipio() != null ? usuario.getCMunicipio().getNombre():"")
                .municipioClave(usuario.getCMunicipio() != null ? usuario.getCMunicipio().getClave() : 0)
                .cLocalidad(usuario.getCLocalidad() != null ? usuario.getCLocalidad().getId() : 0)                
                .localidad(usuario.getCLocalidad() != null ? usuario.getCLocalidad().getNombre():"")
                .localidadClave(usuario.getCLocalidad() != null ? usuario.getCLocalidad().getClave() : 0)
                .cSeccion(usuario.getCSeccion() != null ? usuario.getCSeccion().getId():0)                
                .seccion(usuario.getCSeccion() != null ? usuario.getCSeccion().getNombre():"")
                .SeccionClave(usuario.getCSeccion() != null ? usuario.getCSeccion().getClave() : 0)
                .build();

    }

    public Usuario find(String correo) {
        try {
            return (Usuario) em.createNamedQuery("Usuario.findByCorreo").setParameter(1, correo).getSingleResult();
        } catch (NoResultException nre) {
            log.warn("No encontró el correo del usuario {} ", correo, nre);
            return null;
        }
    }

    public Usuario findById(Integer id) {
        try {
            return (Usuario) em.createNamedQuery("Usuario.findById").setParameter(1, id).getSingleResult();
        } catch (NoResultException nre) {
            log.warn("No encontró el id del usuario {} ", id, nre);
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
        vo.setEmail(u.getEmail());
        vo.setTelefono(u.getTelefono());
        vo.setSexo(u.getSexo());

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
            usuario.setId(usuarioVO.getId());
            usuario.setNombre(fixName(usuarioVO.getNombre()));
            usuario.setClave(usuarioVO.getClave());
            usuario.setEmail(usuarioVO.getEmail());

            //
            usuario.setTelefono(usuarioVO.getTelefono());

            usuario.setSexo(usuarioVO.getSexo());
            usuario.setFoto("/resources/imagenes/usuarios/usuario.png");

            usuario.setGenero(find(sesion));
            usuario.setFechaGenero(new Date());
            usuario.setEliminado(Constantes.NO_ELIMINADO);
            this.create(usuario);

            v = true;
        } catch (Exception e) {
            LOGGER.fatal(this, "Guardar usuario : : : : : : " + e.getMessage(), e);
        }

        return v;
    }

    public boolean modificarUsuario(UsuarioVO usuarioVO, int idGerencia, int idCampo) {
        boolean v = false;
        try {

            Usuario usuario = this.find(usuarioVO.getId());
            usuario.setNombre(fixName(usuarioVO.getNombre()));
            usuario.setEmail(usuarioVO.getEmail());
            usuario.setTelefono(usuarioVO.getTelefono());
            usuario.setSexo(usuarioVO.getSexo());
            edit(usuario);
            v = true;
        } catch (Exception e) {
            log.warn("Modificando usuario {}", usuarioVO.getId(), e);
        }

        return v;
    }

    public void eliminarUsuario(String sesion, String idUser) {
        Usuario usuario = find(idUser);
        usuario.setClave(encriptar(String.valueOf(generaClave())));
        usuario.setEliminado(Constantes.ELIMINADO);
        usuario.setModifico(find(sesion));
        usuario.setFechaModifico(new Date());
        edit(usuario);
    }

    public void activarUsuario(String sesion, UsuarioVO usuarioVo) {
        Usuario usuario = find(usuarioVo.getId());
        usuario.setEliminado(Constantes.NO_ELIMINADO);
        usuario.setModifico(find(sesion));
        usuario.setFechaModifico(new Date());
        this.edit(usuario);
    }

    public boolean cambioContrasenia(Integer usuario, String c, String confirmarPassword) {
        boolean v;
        Usuario u = find(usuario);

        v = false;//this.notificacionSistemaRemote.enviarClaveIhsa(u.getNombre(), u.getEmail(), confirmarPassword);

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
        usuario.setTelefono(telefono);
        usuario.setModifico(usuario);
        usuario.setFechaModifico(new Date());

        v = false; //this.notificacionSistemaRemote.enviarCorreoCambioClaveIhsa(usuario.getNombre(), usuario.getEmail());
        if (v) {
            //            this.siLogDocumentoRemote.guardarLog(9, usuario.getId(), 1, "Cambio de clave");
            usuario.setClave(nuevaClave);
            this.edit(usuario);
        }
        return v;
    }

    public String encriptar(String text) {

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

    public boolean reinicioClave(Integer sesion, Integer idUser) {
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
            usuario.setClave(encriptar(String.valueOf(clave)));
            usuario.setModifico(find(sesion));
            usuario.setFechaModifico(new Date());
            edit(usuario);
        }
        return v;
    }

    private int generaClave() {
        int valorEntero = (int) Math.floor(Math.random() * (1000 - 10000 + 1) + 10000);
        return valorEntero;
    }

    public void modificaUsuarioDatosSinClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular) {
        Usuario usuario = find(idUser);
        usuario.setNombre(fixName(nombre));
        usuario.setEmail(correo);
        usuario.setTelefono(telefono);
        usuario.setModifico(usuario);
        usuario.setFechaModifico(new Date());
        edit(usuario);
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

            vo.setId((Integer) obj[0]);
            vo.setNombre(String.valueOf(obj[1]));
            vo.setEmail(String.valueOf(obj[2]));

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

            vo.setId((Integer) obj[0]);
            vo.setNombre(String.valueOf(obj[1]));
            vo.setEmail(String.valueOf(obj[2]));

            if (campoActual == 0) {
                usuarios.add(vo);
            } else {
                usuariosList.add(usuarios);
                usuarios = new ArrayList<>();
                usuarios.add(vo);
            }
        }
        if (usuarios != null && !usuarios.isEmpty()) {
            usuariosList.add(usuarios);
        }

        return usuariosList;

    }

    public void modificarDatosUsuario(String sesion, UsuarioVO usuarioVO, int idPuesto) {
        try {

            var usuario = find(usuarioVO.getId());

            usuario.setNombre(usuarioVO.getNombre());
            usuario.setEmail(usuarioVO.getEmail());
            usuario.setTelefono(usuarioVO.getTelefono());
            usuario.setSexo(usuarioVO.getSexo());
            edit(usuario);
        } catch (Exception e) {
            LOGGER.error(this, "Ocurrio una excepcion al modificar el usuario  : : : : : " + e.getMessage(), e);
        }
    }

    public List<UsuarioVO> obtenerListaUsuarios() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
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
        return em.createQuery(qry).getResultList();
    }

    public List<UsuarioVO> obtenerListaUsuarios(Integer rol) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
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

        return em.createQuery(qry).getResultList();
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
                .append("where upper(u.NOMBRE COLLATE \"es_ES\") like upper('%").append(cadena).append("%')")
                .append("and u.ELIMINADO = false and u.interno = true ")
                .append("order by u.NOMBRE asc")
                .append("limit  ").append(numero);

        return em.createNativeQuery(sql.toString()).getResultList();
    }

    public Usuario buscarPorId(String usuarioId) {
        Usuario retVal = null;

        String sql
                = "SELECT *  FROM usuario \n"
                + " WHERE id = ? \n"
                + " AND eliminado = false";

        try {
            Record recUsuario = dbCtx.fetchOne(sql, usuarioId, usuarioId);

            if (recUsuario != null) {
                retVal = recUsuario.into(Usuario.class);
            }
        } catch (DataAccessException e) {
            LOGGER.error(this, "Usuario : {0}", new Object[]{usuarioId}, e);
        }

        return retVal;
    }
}
