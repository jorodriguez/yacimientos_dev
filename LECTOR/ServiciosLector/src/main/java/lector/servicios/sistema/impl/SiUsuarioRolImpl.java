/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import lector.constantes.Constantes;
import lector.dominio.vo.RolVO;
import lector.dominio.vo.UsuarioRolVo;
import lector.modelo.SiRol;
import lector.modelo.SiUsuarioRol;
import lector.modelo.Usuario;
import lector.modelo.usuario.vo.UsuarioVO;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.sistema.AbstractFacade;
import lector.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SiUsuarioRolImpl extends AbstractFacade<SiUsuarioRol>{

    @PersistenceContext(unitName =  Constantes.PERSISTENCE_UNIT)
    private EntityManager em;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SiRolImpl siRolRemote;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiUsuarioRolImpl() {
        super(SiUsuarioRol.class);
    }

    /**
     * Creo: NLopez
     *
     * @param rolId
     * @param usuario
     * @param idCampo
     * @return
     * @throws java.lang.Exception
     */
    
    public UsuarioRolVo findUsuarioRolVO(Integer rolId, String usuario, int idCampo) throws Exception {
        clearQuery();
        UsuarioRolVo retVal = null;

        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select ur.id, ur.usuario, ur.si_rol ")
                    .append(" from SI_USUARIO_ROL ur where ")
                    .append(" ur.si_rol= ").append(rolId)
                    .append(" and ur.ap_campo = ").append(idCampo)
                    .append(" and ur.usuario = '").append(usuario).append("' ")
                    .append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");

//            UsuarioRolVo usuarioRol = new UsuarioRolVo();
            retVal = new UsuarioRolVo();

            UtilLog4j.log.info(this, "Q: " + sql.toString());
            Object[] lo = (Object[]) em.createNativeQuery(sql.toString()).getSingleResult();
            if (lo != null) {
                retVal.setIdUsuarioRol((Integer) lo[0]);
                retVal.setIdUsuario((String) lo[1]);
                retVal.setIdRol((Integer) lo[2]);
            }
        } catch (NoResultException e) {
            UtilLog4j.log.error(e);
        } catch (Exception e) {
            UtilLog4j.log.error(this, "", e);
        }
        return retVal;
    }

    
    public UsuarioRolVo findNombreUsuarioRolVO(int rolId, String nombreUsuario, int idCampo) {
        clearQuery();
        try {
            query.append("select ur.id,ur.usuario,ur.si_rol  from SI_USUARIO_ROL ur where ur.USUARIO =");
            query.append(" (select u.id from USUARIO u where u.NOMBRE = '").append(nombreUsuario).append("'").append(" and u.activo = '").append(Constantes.BOOLEAN_TRUE).append("')");
            query.append(" and ur.si_rol= ").append(rolId);
            query.append(" and ur.ap_campo = ").append(idCampo);
            query.append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("' ");
            UsuarioRolVo usuarioRol = new UsuarioRolVo();
            UtilLog4j.log.info(this, "Q: " + query.toString());
            Object[] lo = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            if (lo != null) {
                usuarioRol.setIdUsuarioRol((Integer) lo[0]);
                usuarioRol.setIdUsuario((String) lo[1]);
                usuarioRol.setIdRol((Integer) lo[2]);

            }
            return usuarioRol;
        } catch (NonUniqueResultException u) {
            UtilLog4j.log.info(this, "hay mas de un resultado");
            return new UsuarioRolVo();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No recupero nada nombre usuario: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creo: NLopez
     *
     * @param rol
     * @param usuario
     * @param principal
     * @param idCampo
     */
    
    public void guardar(RolVO rol, String usuario, boolean principal, int idCampo) {
        Usuario user = new Usuario(usuario);
        SiRol r = siRolRemote.find(rol.getId());

        SiUsuarioRol ur = new SiUsuarioRol();
        ur.setUsuario(user);        
        ur.setGenero(new Usuario(Constantes.USUARIO_DEFAULT));
        ur.setSiRol(r);
        ur.setModifico(new Usuario(Constantes.USUARIO_DEFAULT));
        ur.setEliminado(Constantes.BOOLEAN_FALSE);
        ur.setPrincipal(principal);
        ur.setFechaGenero(new Date());
        ur.setHoraGenero(new Date());

        create(ur);
    }

    /**
     * Creo: mluis
     *
     * @param idRol
     * @param nombreUsuario
     * @param principal
     * @param idSesion
     * @param idCampo
     * @return
     */
    
    public boolean guardarUsuarioRol(int idRol, String nombreUsuario, boolean principal, String idSesion, int idCampo) {
        boolean v;
        try {
            SiUsuarioRol ur = new SiUsuarioRol();
            ur.setSiRol(siRolRemote.find(idRol));
            ur.setUsuario(usuarioRemote.buscarPorNombre(nombreUsuario));
            ur.setGenero(new Usuario(idSesion));
            ur.setFechaGenero(new Date());
            ur.setHoraGenero(new Date());
            ur.setEliminado(Constantes.BOOLEAN_FALSE);
            ur.setPrincipal(principal);
            create(ur);
            v = true;
        } catch (Exception e) {
            e.getStackTrace();
            v = false;
        }
        return v;
    }

    /**
     */
    
    public void modificarRolUsuario(UsuarioVO usuario, Integer rolAnterior, int idCampo) throws Exception {
        UsuarioRolVo ura = findUsuarioRolVO(rolAnterior, usuario.getId(), idCampo);

        SiUsuarioRol ur = find(ura.getIdUsuarioRol());

        ur.setModifico(new Usuario(Constantes.USUARIO_DEFAULT));
        ur.setFechaModifico(new Date());
        ur.setHoraModifico(new Date());
        ur.setPrincipal(Constantes.BOOLEAN_FALSE);
        edit(ur);

        UsuarioRolVo urn = findUsuarioRolVO(usuario.getRolId(), usuario.getId(), idCampo);

        SiUsuarioRol urnu = find(urn.getIdUsuarioRol());

        urnu.setModifico(new Usuario(Constantes.USUARIO_DEFAULT));
        urnu.setFechaModifico(new Date());
        urnu.setHoraModifico(new Date());
        urnu.setPrincipal(Constantes.BOOLEAN_TRUE);
        edit(urnu);
    }

    
    public UsuarioRolVo traerRolPrincipal(String idUsuario, int idModulo, int idCampo) {
        UsuarioRolVo retVal = null;
        StringBuilder sql = new StringBuilder();

        try {

            sql.append("SELECT DISTINCT ON (u.id) ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.telefono \n"
                    + "FROM si_usuario_rol ur, usuario u, si_rol r \n"
                    + "WHERE u.id = '").append(idUsuario).append("' \n");

            if (idModulo != 0) {
                sql.append("    AND r.si_modulo = ").append(idModulo).append('\n');
            }

            sql.append("    AND ur.ap_campo = ").append(idCampo).append('\n')
                    .append("   AND ur.usuario = u.id \n")
                    .append("   AND ur.si_rol = r.id \n")
                    .append("   AND ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("' \n")
                    .append("   AND ur.is_principal ='").append(Constantes.BOOLEAN_TRUE).append("' \n")
                    .append("   AND u.eliminado = '").append(Constantes.NO_ELIMINADO).append("' \n")
                    .append("ORDER BY u.id");

            Object[] lo = (Object[]) em.createNativeQuery(sql.toString()).getSingleResult();

            if (lo != null) {
                retVal = castUsuarioRolVo(lo);
            }

        } catch (NoResultException e) {
            UtilLog4j.log.fatal(this, "Usuario : " + idUsuario, e);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + sql.toString(), e);

            retVal = null;
        }

        return retVal;
    }

    
    public List<UsuarioRolVo> traerRolPorUsuarioModulo(String idUsuario, int idModulo, int idCampo) {
        try {
            clearQuery();

            List<UsuarioRolVo> list = new ArrayList<>();
            query.append("select  ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.TELEFONO from si_usuario_rol ur, usuario u, si_rol r");
            query.append(" where u.id = '").append(idUsuario).append("'");
            if (idModulo != 0) {
                query.append(" and r.si_modulo = ").append(idModulo);
            }
            query.append(" and ur.ap_campo = ").append(idCampo);
            query.append(" and ur.usuario = u.id");
            query.append(" and ur.si_rol = r.id");
            query.append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            UtilLog4j.log.info(this, "Q: rol por modulo " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (!lo.isEmpty()) {
                for (Object[] objects : lo) {
                    list.add(castUsuarioRolVo(objects));
                }
            }
            return list;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + e.getMessage());
            return new ArrayList<>();
        }
    }

    
    public List<UsuarioRolVo> traerRolPorNombreUsuarioModulo(String nombreUsuario, int idModulo, int idCampo) {
        try {
            clearQuery();

            List<UsuarioRolVo> list = null;
            query.append("select  ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.TELEFONO from si_usuario_rol ur, usuario u, si_rol r");
            query.append(" where u.nombre = '").append(nombreUsuario.trim()).append("'");
            if (idModulo != 0) {
                query.append(" and r.si_modulo = ").append(idModulo);
            }
            query.append(" and ur.ap_campo = ").append(idCampo);
            query.append(" and ur.usuario = u.id");
            query.append(" and ur.si_rol = r.id");
            query.append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (!lo.isEmpty()) {
                list = new ArrayList<UsuarioRolVo>();
                for (Object[] objects : lo) {
                    list.add(castUsuarioRolVo(objects));
                }
            }
            return list;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + e.getMessage());
            return null;
        }
    }

    
    public List<UsuarioRolVo> traerUsuarioPorRolModulo(int idRol, int idModulo, int idBloque) {
        try {
            clearQuery();
            List<UsuarioRolVo> list = null;
            query.append("select ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.TELEFONO from SI_USUARIO_ROL ur ");
            query.append("	join USUARIO  u on ur.USUARIO = u.ID and u.ACTIVO = '").append(Constantes.BOOLEAN_TRUE).append("'");
            query.append("	join SI_ROL r on r.ID = ur.SI_ROL and r.ID =").append(idRol).append(" and r.SI_MODULO =").append(idModulo);
            query.append(" where ur.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
            query.append(" and u.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
            query.append(" and ur.ap_campo = ").append(idBloque);
            query.append(" order by u.nombre asc");
            UtilLog4j.log.info(this, " ******************* " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (!lo.isEmpty()) {
                list = new ArrayList<UsuarioRolVo>();
                for (Object[] objects : lo) {
                    list.add(castUsuarioRolVo(objects));
                }
            }
            return list;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + e.getMessage());
            return null;
        }
    }

    
    public List<UsuarioRolVo> traerUsuarioPorRolModuloCategoria(int idRol, int idModulo, int idBloque) {
        try {
            clearQuery();
            List<UsuarioRolVo> list = null;

            query.append(" select ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.TELEFONO, ug.GERENCIA ");
            query.append(" from SI_USUARIO_ROL ur ");
            query.append(" inner join USUARIO  u on ug.USUARIO = u.ID and u.ACTIVO = 'True'  ");
            query.append(" inner join SI_ROL r on r.ID = ug.SI_ROL  ");
            query.append(" where ur.ELIMINADO = 'False'  ");
            query.append(" and ur.ap_campo = ").append(idBloque);
            query.append(" and r.ID = ").append(idRol);
            query.append(" and r.SI_MODULO = ").append(idModulo);
            query.append(" and u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" order by u.nombre asc ");

            UtilLog4j.log.info(this, " ******************* " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (!lo.isEmpty()) {
                list = new ArrayList<UsuarioRolVo>();
                for (Object[] objects : lo) {
                    list.add(castUsuarioRolVo(objects));
                }
            }
            return list;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + e.getMessage());
            return null;
        }
    }

    
    public UsuarioRolVo traerRolUsuarioModulo(int idRol, int idModulo, boolean principal, int idCampo) {
        try {
            clearQuery();
            query.append("select  ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.TELEFONO from si_usuario_rol ur, usuario u, si_rol r");
            query.append(" where r.si_modulo = ").append(idModulo);
            query.append(" and r.id = ").append(idRol);
            query.append(" and ur.ap_campo = ").append(idCampo);
            query.append(" and ur.usuario = u.id");
            query.append(" and ur.si_rol = r.id");
            query.append(" and ur.is_principal = '").append(principal).append("'");
            query.append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" order by ur.id");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();

            Object[] objects = lo.get(Constantes.CERO);
            return castUsuarioRolVo(objects);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + e.getMessage());
            return null;
        }
    }

    
    public List<UsuarioRolVo> traerUsuarioByRol(List<Integer> listaRoles, int idCampo) {
        try {
            String cad = listaRoles.toString();
            String cadenaLista = cad.substring(1, cad.length() - 1);
            clearQuery();
            query.append("select ur.ID, u.ID, u.NOMBRE, r.ID, r.NOMBRE, ur.is_principal, u.EMAIL, u.TELEFONO ");
            query.append(" from SI_USUARIO_ROL ur, USUARIO u, SI_ROL r ");
            query.append(" where ur.SI_ROL in ( ").append(cadenaLista);
            query.append(" ) and ur.USUARIO = u.id and ur.SI_ROL = r.id ");
            query.append(" and ur.ap_campo = ").append(idCampo);
            query.append(" and u.ELIMINADO = 'False' and r.ELIMINADO = 'False' and ur.ELIMINADO = 'False' ");
            // 
            UtilLog4j.log.info(this, " query roles ");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<UsuarioRolVo> lu = new ArrayList<>();
            for (Object[] objects : lo) {
                lu.add(castUsuarioRolVo(objects));
            }

            return lu;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    private UsuarioRolVo castUsuarioRolVo(Object[] objects) {
        UsuarioRolVo usuarioRolVo = new UsuarioRolVo();
        usuarioRolVo.setIdUsuarioRol((Integer) objects[0]);
        usuarioRolVo.setIdUsuario((String) objects[1]);
        usuarioRolVo.setUsuario((String) objects[2]);
        usuarioRolVo.setIdRol((Integer) objects[3]);
        usuarioRolVo.setNombreRol((String) objects[4]);
        usuarioRolVo.setPrincipal((Boolean) objects[5]);
        usuarioRolVo.setCorreo(objects[6] != null ? (String) objects[6] : "");
        usuarioRolVo.setTelefono(objects[7] != null ? (String) objects[7] : "");

        return usuarioRolVo;
    }

    
    public void eliminarUsuarioRol(int idUr, String idSesion) {
        SiUsuarioRol ur = find(idUr);
        String ae = ur.toString();
        ur.setModifico(new Usuario(idSesion));
        ur.setFechaModifico(new Date());
        ur.setHoraModifico(new Date());
        ur.setPrincipal(Constantes.BOOLEAN_FALSE);
        ur.setEliminado(Constantes.ELIMINADO);
        edit(ur);
    }

    
    public List<UsuarioRolVo> traerRolPorCodigo(String rol, int campo, int modulo) {
        try {
            clearQuery();

            List<UsuarioRolVo> list = null;
            query.append("select  ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.TELEFONO from si_usuario_rol ur");
            query.append("      inner join usuario u on ur.usuario = u.id ");
            query.append("      inner join si_rol r on ur.si_rol = r.id ");
            query.append(" where r.codigo = '").append(rol).append("'");
            query.append(" and  ur.ap_campo = ").append(campo);
            query.append(" and  r.si_modulo = ").append(modulo);
            query.append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (!lo.isEmpty()) {
                list = new ArrayList<>();
                for (Object[] objects : lo) {
                    list.add(castUsuarioRolVo(objects));
                }
            }
            return list;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + e.getMessage());
            return null;
        }
    }

    
    public boolean buscarRolPorUsuarioModulo(String idUsuario, int idModulo, String codigo, int campo) {
        boolean v = false;
        try {
            clearQuery();
            query.append("select  ur.id, u.id, u.nombre, r.id, r.nombre, ur.is_principal, u.email, u.TELEFONO from si_usuario_rol ur, usuario u, si_rol r");
            query.append(" where u.id = '").append(idUsuario).append("'");
            query.append(" and r.CODIGO = '").append(codigo).append("'");
            query.append(" and r.si_modulo = ").append(idModulo);
            if (campo > 0) {
                query.append(" and ur.ap_campo = ").append(campo);
            }
            query.append(" and ur.usuario = u.id");
            query.append(" and ur.si_rol = r.id");
            query.append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" and u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (!lo.isEmpty()) {
                v = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el rol por usuario y modulo " + e.getMessage());
        }
        return v;
    }

    
    public int guardar(int rol, String usuario, boolean principal, int idCampo, String sesion) {
        SiUsuarioRol ur = new SiUsuarioRol();
        ur.setUsuario(new Usuario(usuario));
        ur.setGenero(new Usuario(sesion));
        ur.setSiRol(new SiRol(rol));
        ur.setEliminado(Constantes.NO_ELIMINADO);
        ur.setPrincipal(principal ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        ur.setFechaGenero(new Date());
        ur.setHoraGenero(new Date());

        create(ur);
        //
        return ur.getId();
    }

    
    public String traerCorreosByRolList(List<Integer> listaRoles, int idCampo) {
        try {
            String cad = listaRoles.toString();
            String cadenaLista = cad.substring(1, cad.length() - 1);
            clearQuery();
            //
            query.append("select  COALESCE(array_to_string(array_agg(DISTINCT u.EMAIL), ', '), '') from SI_USUARIO_ROL ur \n")
                    .append("	inner join usuario u on ur.USUARIO = u.id \n")
                    .append(" where ur.SI_ROL in ( ").append(cadenaLista).append(")")
                    .append(" and ur.ap_campo = ").append(idCampo).append("  and u.ELIMINADO = false  and ur.ELIMINADO = false ");

            UtilLog4j.log.info(this, " query roles " + query);
            return (String) em.createNativeQuery(query.toString()).getSingleResult();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "";
    }

    
    public List<String> traerCorreosByRolAndOficina(String rol, int idoficina, int idCampo) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select ur.ID, u.ID, u.NOMBRE, r.ID, r.NOMBRE, ur.is_principal, u.EMAIL, u.TELEFONO "
                    + " from SI_USUARIO_ROL ur, USUARIO u, SI_ROL r "
                    + " where ur.SI_ROL = r.ID and r.CODIGO = ? "
                    + " and ur.USUARIO = u.id and ur.SI_ROL = r.id "
                    + " and ur.ap_campo = ? "
                    + " and u.SG_OFICINA = ? "
                    + " and u.ELIMINADO = ? and r.ELIMINADO = ? and ur.ELIMINADO = ? ");

            List<Object[]> lo = em.createNativeQuery(sb.toString())
                    .setParameter(1, rol)
                    .setParameter(2, idCampo)
                    .setParameter(3, idoficina)
                    .setParameter(4, Constantes.NO_ELIMINADO)
                    .setParameter(5, Constantes.NO_ELIMINADO)
                    .setParameter(6, Constantes.NO_ELIMINADO)
                    .getResultList();
            List<String> lu = new ArrayList<String>();
            for (Object[] objects : lo) {
                lu.add((String) objects[6]);
            }

            return lu;
        } catch (Exception e) {
            e.getStackTrace();
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    
    public String traerCorreosPorCodigoRolList(String codigorol, int idCampo) {
        try {
            clearQuery();
            query.append("select COALESCE(array_to_string(array_agg(DISTINCT u.EMAIL), ', '), '') from SI_USUARIO_ROL ur \n")
                    .append("	inner join usuario u on ur.USUARIO = u.id \n")
                    .append("	inner join si_rol r on ur.si_rol = r.id \n")
                    .append(" where r.codigo = '").append(codigorol).append("'")
                    .append(" and ur.ap_campo = ").append(idCampo)
                    .append(" and u.ELIMINADO = false  and ur.ELIMINADO = false ");

            UtilLog4j.log.info(this, " query roles " + query);
            return (String) em.createNativeQuery(query.toString()).getSingleResult();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "";
    }

    
    public String correosListaDestinatarios(int apCampoID, String listaNombre) {
        String emails = "";
        String sb
                = " select  COALESCE(array_to_string(array_agg(DISTINCT d.email), ', '), '')  "
                + " from destinatarios_correo_vw d  "
                + " inner join lista_correo l on l.id = d.id "
                + " where d.ap_campo = " + apCampoID
                + " and l.nombre = '" + listaNombre + "' ";

        Object obj = em.createNativeQuery(sb)
                .getSingleResult();

        if (obj != null) {
            emails = String.valueOf(obj);
        }
        return emails;
    }

    
    public void guardarUsuarioRol(String codigoRol, String idUsuario, int idCampo, String sesion) {
        SiUsuarioRol ur = new SiUsuarioRol();
        SiRol rol = siRolRemote.buscarPorCodigo(codigoRol);
        ur.setUsuario(new Usuario(idUsuario));
        ur.setGenero(new Usuario(sesion));
        ur.setSiRol(rol);
        ur.setEliminado(Constantes.NO_ELIMINADO);
        ur.setPrincipal(Constantes.BOOLEAN_FALSE);
        ur.setFechaGenero(new Date());
        ur.setHoraGenero(new Date());

        create(ur);
    }
    
    
    public String traerUsuarioPorCodigoRolList(String codigorol, int idCampo) {
        try {
            clearQuery();
            query.append("select COALESCE(array_to_string(array_agg(DISTINCT u.nombre), ', '), '') from SI_USUARIO_ROL ur \n")
                    .append("	inner join usuario u on ur.USUARIO = u.id \n")
                    .append("	inner join si_rol r on ur.si_rol = r.id \n")
                    .append(" where r.codigo = '").append(codigorol).append("'")
                    .append(" and ur.ap_campo = ").append(idCampo)
                    .append(" and u.ELIMINADO = false  and ur.ELIMINADO = false ");

            UtilLog4j.log.info(this, " query usuarios " + query);
            return (String) em.createNativeQuery(query.toString()).getSingleResult();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return "";
    }
}
