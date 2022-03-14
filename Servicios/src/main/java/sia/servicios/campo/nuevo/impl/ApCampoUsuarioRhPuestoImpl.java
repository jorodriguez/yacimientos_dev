/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.campo.nuevo.impl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.ApCampoUsuarioRhPuesto;
import sia.modelo.Gerencia;
import sia.modelo.RhPuesto;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class ApCampoUsuarioRhPuestoImpl extends AbstractFacade<ApCampoUsuarioRhPuesto> {

    private static final UtilLog4j LOGGER = UtilLog4j.log;
    
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Inject
    DSLContext dbCtx;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private RhPuestoImpl rhPuestoRemote;    

    public ApCampoUsuarioRhPuestoImpl() {
        super(ApCampoUsuarioRhPuesto.class);
    }

    
    public String getPuestoPorUsurioCampo(String userId, int campo) {
        String retVal;
        
        try {
            
            final String sql =
                    "SELECT p.nombre, p.id "
                    + " FROM ap_campo_usuario_rh_puesto cup \n"
                    + "      INNER JOIN rh_puesto p ON cup.rh_puesto = p.id \n"
                    + " WHERE cup.usuario  = ? \n"
                    + " AND cup.ap_campo = ? \n"
                    + " AND p.eliminado = 'False'\n"
                    + " AND cup.eliminado = 'False'\n";
            
            LOGGER.info(this, "Q para recuperar el puesto: {0} - {1} - {2}", new Object[]{userId, campo, sql});
            
            final Object[] obj = 
                    (Object[]) em.createNativeQuery(sql)
                            .setParameter(1, userId)
                            .setParameter(2, campo)
                            .getSingleResult();
            
            retVal = (String) obj[0];
        } catch (Exception e) {
            LOGGER.fatal(this, "", e);
            retVal = Constantes.VACIO;
        }
        
        return retVal;
    }

    
    public CampoUsuarioPuestoVo traerPuestoPorUsuarioCampo(String userId, int campo) {
        
        CampoUsuarioPuestoVo cup = new CampoUsuarioPuestoVo();
        
        try {
            
            final String sql = "SELECT cup.id, c.nombre,  p.nombre, c.id, p.id, u.id, u.nombre, c.compania \n"
            + "FROM ap_campo_usuario_rh_puesto cup, usuario u, rh_puesto p, ap_campo c  \n"
            + "WHERE cup.USUARIO  = ?  \n"
            + " AND cup.AP_CAMPO = ?  \n"
            + " AND cup.usuario = u.id \n"
            + " AND cup.AP_CAMPO = c.id \n"
            + " AND cup.RH_PUESTO = p.id \n"
            + " AND p.eliminado = 'False' \n"
            + " AND cup.eliminado = 'False'";
            
            LOGGER.info(this, "Q para recuperar el puesto: {0} - {1} - {2} ", new Object[]{userId, campo, sql});
        
            final Object[] obj = 
                    (Object[]) em.createNativeQuery(sql)
                            .setParameter(1, userId)
                            .setParameter(2, campo)
                            .getSingleResult();
            
            cup = castPuesto(obj);
            
        } catch (Exception e) {
            LOGGER.fatal(this, "", e);
        }
        
        return cup;
    }

    
    public void save(String sesion, int idCampo, String idUser, int idPuesto, int gerencia) {
        ApCampoUsuarioRhPuesto apCampoUsuarioRhPuesto = new ApCampoUsuarioRhPuesto();
        apCampoUsuarioRhPuesto.setApCampo(new ApCampo(idCampo));
        apCampoUsuarioRhPuesto.setUsuario(new Usuario(idUser));
        apCampoUsuarioRhPuesto.setRhPuesto(new RhPuesto(idPuesto));
        apCampoUsuarioRhPuesto.setGenero(new Usuario(sesion));
        apCampoUsuarioRhPuesto.setFechaGenero(new Date());
        apCampoUsuarioRhPuesto.setHoraGenero(new Date());
        apCampoUsuarioRhPuesto.setEliminado(Constantes.NO_ELIMINADO);
        apCampoUsuarioRhPuesto.setGerencia(new Gerencia(gerencia));
        create(apCampoUsuarioRhPuesto);
    }

    
    public List<CampoUsuarioPuestoVo> getAllPorUsurio(String userId) {
        
        final String sql = 
                "SELECT cup.id AS id_campo_usuario_puesto, c.nombre as campo,  \n"
                + " p.nombre AS puesto, c.id AS id_campo, p.id AS id_puesto, \n"
                + " u.id AS id_usuario, u.nombre AS usuario, c.compania AS rfc_compania, c.tipo, g.id as idGerencia, g.nombre as gerencia\n"
                + "FROM ap_campo_usuario_rh_puesto cup \n"
                + "       INNER JOIN usuario u ON cup.usuario = u.id \n"
                + "       INNER JOIN rh_puesto p ON cup.rh_puesto = p.id \n"
                + "       INNER JOIN ap_campo c ON cup.ap_campo = c.id \n"
                + "       INNER JOIN gerencia g ON cup.gerencia = g.id \n"
                + "WHERE cup.usuario  = ? \n"
                + " AND u.activo = 'True' \n"
                + " AND cup.eliminado = 'False' \n"
                + " AND c.eliminado = 'False' \n"
                + " AND p.eliminado = 'False' \n"
                + "ORDER BY c.nombre asc";
        
        List<CampoUsuarioPuestoVo> voList = Collections.emptyList();

        try {
            voList = dbCtx.fetch(sql, userId).into(CampoUsuarioPuestoVo.class);
        } catch (Exception e) {
            LOGGER.warn(this, "query campos usuarios : {0} - {1}", new Object[]{userId, sql}, e);
            voList = Collections.emptyList();
        }

        return voList;
    }

    
    public List<CampoUsuarioPuestoVo> getAllPorUsurioCategoria(String userId, int idRol) {
               
        String sql = 
                " SELECT cup.id AS id_campo_usuario_puesto, c.nombre AS campo, \n"
                + " p.nombre AS puesto, c.id AS id_campo, p.id AS id_puesto, \n"
                + " u.id AS id_usuario, u.nombre AS usuario, c.compania AS rfc_compania \n"
                + "FROM ap_campo_usuario_rh_puesto cup \n"
                + " INNER JOIN usuario u on u.ID = cup.USUARIO AND u.activo = 'True' \n"
                + " INNER JOIN rh_puesto p on p.ID = cup.RH_PUESTO and p.eliminado = 'False' \n"
                + " INNER JOIN ap_campo c on c.ID = cup.AP_CAMPO and c.eliminado = 'False' \n"
                + " LEFT JOIN si_usuario_rol urgg on urgg.AP_CAMPO = cup.AP_CAMPO and urgg.USUARIO = cup.USUARIO and urgg.ELIMINADO = 'False' \n"
                + "WHERE cup.usuario  = ? \n"
                + " AND cup.eliminado = 'False'\n"
                + " AND urgg.si_rol = ? \n"
                + "GROUP BY cup.id, c.nombre,  p.nombre, c.id, p.id, u.id, u.nombre, c.compania";
        
        List<CampoUsuarioPuestoVo> voList = Collections.emptyList();
        
        try {
            voList = dbCtx.fetch(sql, userId, idRol).into(CampoUsuarioPuestoVo.class);
        } catch (Exception e) {
            System.out.println("E: " + e);
            LOGGER.info(this, "query campos usuarios : {0} - {1} - {2}", new Object[]{userId, idRol, sql});
            LOGGER.error(this, "******", e);
        }
        
        return voList;
    }
    
    
    public List<CampoUsuarioPuestoVo> getAllPorUsurioCategoriaCodigo(String userId, String codigo) {
               
        String sql = 
                " SELECT cup.id AS id_campo_usuario_puesto, c.nombre AS campo, \n"
                + " p.nombre AS puesto, c.id AS id_campo, p.id AS id_puesto, \n"
                + " u.id AS id_usuario, u.nombre AS usuario, c.compania AS rfc_compania \n"
                + "FROM ap_campo_usuario_rh_puesto cup \n"
                + " INNER JOIN usuario u on u.ID = cup.USUARIO AND u.activo = true \n"
                + " INNER JOIN rh_puesto p on p.ID = cup.RH_PUESTO and p.eliminado = false \n"
                + " INNER JOIN ap_campo c on c.ID = cup.AP_CAMPO and c.eliminado = false \n"
                + " LEFT JOIN si_usuario_rol urgg on urgg.AP_CAMPO = cup.AP_CAMPO and urgg.USUARIO = cup.USUARIO and urgg.ELIMINADO = false \n"
                + " left join si_rol r on r.id = urgg.si_rol and r.eliminado = false \n"
                + "WHERE cup.usuario  = ? \n"
                + " AND cup.eliminado = false \n"
                + " AND r.codigo = ? \n"
                + "GROUP BY cup.id, c.nombre,  p.nombre, c.id, p.id, u.id, u.nombre, c.compania";
        
        List<CampoUsuarioPuestoVo> voList = Collections.emptyList();
        
        try {
            voList = dbCtx.fetch(sql, userId, codigo).into(CampoUsuarioPuestoVo.class);
        } catch (Exception e) {
            System.out.println("E: " + e);
            LOGGER.info(this, "query campos usuarios : {0} - {1} - {2}", new Object[]{userId, codigo, sql});
            LOGGER.error(this, "******", e);
        }
        
        return voList;
    }

    
    public List<CampoUsuarioPuestoVo> traerUsurioPorCampo(int idCampo, UsuarioVO vo) {
        
        StringBuffer sql = new StringBuffer(
                "SELECT cup.id AS id_campo_usuario_puesto, c.nombre AS campo, \n" +
                "   p.nombre AS puesto, c.id AS id_campo, p.id AS id_puesto, \n" +
                "   u.id AS id_usuario, u.nombre AS usuario, c.compania AS rfc_compania \n" +
                "FROM ap_campo_usuario_rh_puesto cup\n" +
                "	INNER JOIN usuario u ON cup.usuario = u.id AND u.activo = 'True' \n" +
                "	INNER JOIN rh_puesto p ON cup.rh_puesto = p.id AND p.eliminado = 'False' \n" +
                "	INNER JOIN ap_campo c ON cup.ap_campo = c.id AND c.eliminado = 'False' \n" +
                "WHERE cup.ap_campo  = ? \n" +
                "	AND cup.eliminado = 'False' \n" +
                "	AND u.eliminado = 'False' "
        );
        
        if (vo != null && (vo.getIdGerencia() == 23 || vo.getIdGerencia() == 37)) {
            sql.append(" AND u.gerencia IN (23,37)");
        }                
        
        sql.append(" ORDER BY u.nombre asc");
        
        LOGGER.info(this, "query usuarios por campo: {0} - {1} - {2}", new Object[]{vo, idCampo, sql});
        
        List<CampoUsuarioPuestoVo> voList = Collections.emptyList();

        try {
            voList = dbCtx.fetch(sql.toString(), idCampo).into(CampoUsuarioPuestoVo.class);
        } catch (Exception e) {
            LOGGER.error(this, "query usuarios por campo: {0} - {1} - {2}", new Object[]{vo, idCampo, sql}, e);
        }
        
        return voList;
    }
    
    
    public List<SelectItem> traerUsurioEnCampoPorCadenaItems(String cadena, int idCampo) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        cadena = cadena.toUpperCase();
        for (CampoUsuarioPuestoVo p : this.traerUsurioEnCampoPorCadena(cadena, idCampo)) {
            if (p.getUsuario() != null) {                                
                    SelectItem item = new SelectItem(p, p.getUsuario());
                    list.add(item);                
            }
        }
        return list;
    
    }
    
    
    public List<CampoUsuarioPuestoVo> traerUsurioEnCampoPorCadena(String cadena, int idCampo) {
        
        StringBuffer sql = new StringBuffer(
                "SELECT 0, '0', '0', 0, 0, u.id, u.nombre, '0' \n"
                + "FROM ap_campo_usuario_rh_puesto cup, usuario u, rh_puesto p, ap_campo c \n"
                + "WHERE cup.eliminado = 'False' \n");
        
        String like = null;
        int params = 0;
        
        if(idCampo > 0){
            sql.append("    AND cup.ap_campo  = ? \n");
            params++;
        }
        
        sql.append(
                "   AND cup.usuario = u.id \n"
                + " AND cup.ap_campo = c.id \n"
                + " AND cup.rh_puesto = p.id \n"
                + " AND u.activo = 'True' \n"        
                + " AND c.eliminado = 'False' \n"
                + " AND p.eliminado = 'False' \n"
                + " AND u.eliminado = 'False' \n");
        
        if(!Strings.isNullOrEmpty(cadena)){
            sql.append(" AND upper(u.nombre) LIKE ? \n");
            
            like = '%' + cadena + '%';
            params++;
        }
        
        sql.append("GROUP BY u.id, u.nombre \n" 
                + "ORDER BY u.nombre ASC");
        
        LOGGER.info(this, "query usuarios por campo: {0} - {1} - {2}", new Object[]{idCampo, like, sql});
        
        Query nativeQry = em.createNativeQuery(sql.toString());
        
        if(params == 1 && idCampo > 0) {
            nativeQry = nativeQry.setParameter(1, idCampo);
        } else if(params == 1 && like != null) {
            nativeQry = nativeQry.setParameter(1, like);
        } else if(params == 2) {
            nativeQry = 
                    nativeQry
                            .setParameter(1, idCampo)
                            .setParameter(2, like)
                    ;
        }
        
        final List<Object[]> list = nativeQry.getResultList();
        final List<CampoUsuarioPuestoVo> voList = new ArrayList<CampoUsuarioPuestoVo>();

        for (Object[] objeto : list) {
            voList.add(castPuesto(objeto));
        }
        
        LOGGER.info(this, "" + voList);
        
        return voList;
    }

    
    public List<CampoUsuarioPuestoVo> getCampoPorUsurio(String userId, int campo) {

        List<Object[]> list;
        
        Query q = em.createNativeQuery("SELECT cup.id, c.nombre,  p.nombre, c.id, p.id, u.id, u.nombre , c.compania \n"
                + "FROM ap_campo_usuario_rh_puesto cup, usuario u, rh_puesto p, ap_campo c \n"
                + "WHERE cup.usuario  = ? \n"
                + "\tAND c.id = ? \n"
                + "\tAND cup.usuario = u.id \n"
                + "\tAND cup.ap_campo = c.id \n"
                + "\tAND cup.rh_puesto = p.id \n"
                + "\tAND u.activo = 'True' \n"
                + "\tAND cup.eliminado = 'False' \n"
                + "\tAND c.eliminado = 'False' \n"
                + "\tAND p.eliminado = 'False' \n")
                .setParameter(1, userId)
                .setParameter(2, campo);
        
        LOGGER.info(this, "query verifica campo : {0} - {1} - {2}", new Object[]{userId, campo, q.toString()});
        
        list = q.getResultList();
        
        List<CampoUsuarioPuestoVo> voList = new ArrayList<CampoUsuarioPuestoVo>();

        for (Object[] objeto : list) {
            voList.add(castPuesto(objeto));
        }
        return voList;
    }

    
    public List<CampoUsuarioPuestoVo> traerCampoPorUsurioMenosActual(String userId, int campo) {

        StringBuffer sql = new StringBuffer(
                "SELECT cup.id, c.nombre,  p.nombre, c.id, p.id, u.id, u.nombre, c.compania \n"
                + "FROM ap_campo_usuario_rh_puesto cup, usuario u, rh_puesto p, ap_campo c \n"
                + "WHERE cup.usuario  = ? \n "
        );
        
        if(campo > 0){
            sql.append(" AND c.id <> ? ");
        }
        
        sql.append(
                "\tAND cup.usuario = u.id  AND cup.ap_campo = c.id AND cup.rh_puesto = p.id \n"
                + "\tAND u.activo = 'True' \n"
                + "\tAND cup.eliminado = 'False' \n"
                + "\tAND c.eliminado = 'False' \n"
                + "\tAND p.eliminado = 'False' \n"
        );
        
        LOGGER.debug(this, "**** query verifica campo : {0} - {1} - {2}", new Object[]{userId, campo, sql});
        
        Query queryEm = em.createNativeQuery(sql.toString());
        
        queryEm = queryEm.setParameter(1, userId);
        
        if(campo > 0) {
            queryEm = queryEm.setParameter(2, campo);
        }
        
        final List<Object[]> list = queryEm.getResultList();
        
        List<CampoUsuarioPuestoVo> voList = new ArrayList<CampoUsuarioPuestoVo>();
        
        for (Object[] objeto : list) {
            voList.add(castPuesto(objeto));
        }
        return voList;
    }

    private CampoUsuarioPuestoVo castPuesto(Object[] objeto) {
        CampoUsuarioPuestoVo vo = new CampoUsuarioPuestoVo();
        vo.setIdCampoUsuarioPuesto((Integer) objeto[0]);
        vo.setCampo(String.valueOf(objeto[1]));
        vo.setPuesto(String.valueOf(objeto[2]));
        vo.setIdCampo((Integer) objeto[3]);
        vo.setIdPuesto((Integer) objeto[4]);
        vo.setIdUsuario((String) objeto[5]);
        vo.setUsuario((String) objeto[6]);
        vo.setRfcCompania((String) objeto[7]);
        vo.setSelected(false);
        return vo;

    }

    
    public void edit(String sesion, int campo, String usuario, int puesto, int campoUsuario) {
        ApCampoUsuarioRhPuesto vo = find(campoUsuario);
        vo.setApCampo(apCampoRemote.find(campo));
        vo.setRhPuesto(rhPuestoRemote.find(puesto));
        vo.setUsuario(new Usuario(usuario));
        vo.setModifico(new Usuario(sesion));
        vo.setFechaModifico(new Date());
        vo.setHoraModifico(new Date());
        edit(vo);
        //log
    }

    
    public void delete(String sesion, int campoUsuario) {
        ApCampoUsuarioRhPuesto vo = find(campoUsuario);
        vo.setEliminado(Constantes.BOOLEAN_TRUE);
        vo.setModifico(new Usuario(sesion));
        vo.setFechaModifico(new Date());
        vo.setHoraModifico(new Date());
        edit(vo);
        //log
    }

    
    public CampoUsuarioPuestoVo findByUsuarioCampo(int idCampo, String usuario) {
        
        CampoUsuarioPuestoVo retVal = null;
        
        try {
            
            String sql = 
                    "SELECT a.id as Id_Campo_Usuario_Puesto, a.ap_campo as Id_Campo, a.usuario,"
                    + " a.rh_puesto as id_puesto, rh.nombre as puesto,  c.compania as rfc_Compania,"
                    + "  a.gerencia as id_gerencia, g.nombre as gerencia \n"
                    +"FROM ap_campo_usuario_rh_puesto a \n"
                    + "\tINNER JOIN rh_puesto rh on rh.id = a.rh_puesto \n"
                    + "\tINNER JOIN ap_campo c on a.ap_campo = c.id \n"
                    + "\tINNER JOIN gerencia g on g.id = a.gerencia \n"
                    + "WHERE a.ap_campo = ? \n"
                    + "\tAND a.usuario = ? \n"
                    + "\tAND a.eliminado= ?";
            
            
            LOGGER.info(this, "query verifica Usuario Campo : {0} - {1} - {2}", new Object[]{idCampo, usuario, sql});
            
            Record record = dbCtx.fetchOne(sql,idCampo,usuario,Constantes.FALSE);
            
            if(record != null) {
                retVal = record.into(CampoUsuarioPuestoVo.class);
            }
            
        } catch (DataAccessException e) {
            LOGGER.fatal(e);
        }
        
        return retVal;
    }

    //FIXME : determinar por qué se están recibiendo los demás parámetros
    
    public List<Usuario> regresaUsuarioCampo(int idCampo, String nombre, boolean sortAscending, boolean activo, boolean eliminado) {
        List<Usuario> lu = null;
        
        try {
            
            String q = "SELECT cap.usuario FROM ap_campo_usuario_rh_puesto cap WHERE cap.ap_campo =  ? AND cap.eliminado = 'False'";
            List<String> lid = em.createNativeQuery(q)
                    .setParameter(1, idCampo)
                    .getResultList();
            
            for (String string : lid) {
                lu.add(usuarioRemote.find(string));
            }
            
        } catch (Exception e) {
            LOGGER.warn(this, "", e);
        }
        
        return lu;
    }

    /**
     *
     * @param usuarioId
     * @return
     */
    
    public List<CompaniaBloqueGerenciaVo> traerCompaniasBloquesGerencias(String usuarioId) {

        String sql = 
                "SELECT distinct "
                +"  comp.RFC || '-' || bloque.ID || '-' || ger.id AS cup_id, \n"
                +"  comp.RFC AS compania_rfc, \n"
                +"  comp.SIGLAS AS compania_siglas, \n"
                +"  comp.NOMBRE AS compania_nombre, \n"
                +"  bloque.ID AS bloque_id,  \n"
                +"  bloque.NOMBRE AS bloque_nombre, \n"
                +"  cup.gerencia as gerencia_id, \n"
                +"  ger.nombre as gerencia_nombre \n"
                +"FROM \n"
                +"    usuario usu   \n"
                +"    INNER JOIN ap_campo_usuario_rh_puesto cup ON (cup.usuario = usu.id AND usu.activo = 'True' AND cup.eliminado = 'False') \n"
                +"    INNER JOIN ap_campo bloque ON (cup.ap_campo = bloque.id AND bloque.eliminado = 'False')  "
                +"    INNER JOIN compania comp ON (bloque.compania = comp.rfc AND comp.eliminado = 'False')  "
                +"    INNER JOIN gerencia ger on (cup.gerencia = ger.id) "
                +"WHERE usu.id = ? \n"
                +"ORDER BY bloque_nombre ASC";

        List resultado = em.createNativeQuery(sql).setParameter(1, usuarioId).getResultList();

        return castBloquesUsuariosVo(resultado);

    }

    /**
     *
     * @param lista
     * @return
     */
    private List<CompaniaBloqueGerenciaVo> castBloquesUsuariosVo(List lista) {

        List<CompaniaBloqueGerenciaVo> result = new ArrayList();

        for (Iterator<Object[]> it = lista.iterator(); it.hasNext();) {

            result.add(castBloqueUsuarioVo(it.next()));

        }

        return result;

    }

    /**
     *
     * @param obj
     * @return
     */
    private CompaniaBloqueGerenciaVo castBloqueUsuarioVo(Object[] obj) {

        CompaniaBloqueGerenciaVo vo = new CompaniaBloqueGerenciaVo();

        int i = 0;

        //vo.setId((Integer) obj[i++]);
        vo.setRegistroId(String.valueOf(obj[i++]));

        vo.setCompaniaRfc(String.valueOf(obj[i++]));
        vo.setCompaniaSiglas(String.valueOf(obj[i++]));
        vo.setCompaniaNombre(String.valueOf(obj[i++]));

        vo.setBloqueId((Integer) obj[i++]);
        vo.setBloqueNombre(String.valueOf(obj[i++]));

        vo.setGerenciaId((Integer) obj[i++]);
        vo.setGerenciaNombre(String.valueOf(obj[i++]));

        return vo;
    }

    
    public String traerUsuarioActivoPorBloque(int idBloque, int gerencia) {
        
        String retVal = null;
        
        try {
            Gson gson = new Gson();

            List<Object[]> list;
            
            StringBuffer sql = new StringBuffer(
                    "SELECT u.id, u.nombre \n"
                    + "FROM ap_campo_usuario_rh_puesto cup, usuario u \n"
                    + "WHERE cup.ap_campo  = ? \n"
                    + " AND cup.usuario = u.id \n"
                    + " AND u.activo = 'True' \n"
                    + " AND cup.eliminado = 'False'");
            
            if (gerencia == Constantes.GERENCIA_ID_COMPRAS || gerencia == 37) {
                sql.append(" and u.gerencia in (23,37) \n");
            }
            
            sql.append("ORDER by u.nombre asc");
            
            LOGGER.info(this, "query usuarios por campo: {0} - {1} - {2}", new Object[]{idBloque , gerencia, sql});
            
            list = 
                    em.createNativeQuery(sql.toString())
                            .setParameter(1, idBloque)
                            .getResultList();
            
            JsonArray a = new JsonArray();

            for (Object[] o : list) {
                if (list != null) {
                    JsonObject ob = new JsonObject();
                    
                    ob.addProperty("value", o[0] == null ? "-" : (String) o[0]);
                    ob.addProperty("label", o[1] == null ? "-" : (String) o[1]);
                    a.add(ob);
                }
            }
            
            retVal = gson.toJson(a);

        } catch (Exception e) {
            LOGGER.fatal(this, "", e);
        }
        
        return retVal;
    }

    /**
     *
     * @param gerencia
     * @param idCampo
     * @return
     */
    
    public List<UsuarioVO> traerUsurioGerenciaCampo(int gerencia, int idCampo) {
        final String sql = 
                "SELECT u.id, u.nombre, u.email \n"
                + "FROM ap_campo_usuario_rh_puesto cup, usuario u, rh_puesto p, ap_campo c \n"
                + "WHERE cup.gerencia  = ? \n"
                + "  AND cup.ap_campo = ? \n"
                + "  AND cup.usuario = u.id \n"
                + "  AND cup.AP_CAMPO = c.id \n"
                + "  AND cup.RH_PUESTO = p.id \n"
                + "  AND cup.eliminado = 'False' \n"
                + "  AND c.eliminado = 'False' \n"
                + "  AND p.eliminado = 'False' \n"
                + "  AND u.eliminado = 'False' \n"
                + "ORDER BY u.nombre asc";
        
        final List<Object[]> list = 
                em.createNativeQuery(sql)
                        .setParameter(1, gerencia)
                        .setParameter(2, idCampo)
                        .getResultList();
        
        final List<UsuarioVO> voList = new ArrayList<UsuarioVO>();

        for (Object[] objeto : list) {
            voList.add(castUsuario(objeto));
        }
        
        return voList;

    }

    private UsuarioVO castUsuario(Object[] objeto) {
        final UsuarioVO u = new UsuarioVO();
        u.setId((String) objeto[0]);
        u.setNombre((String) objeto[1]);
        u.setMail((String) objeto[2]);
        return u;
    }

    
    public List<UsuarioVO> traerUsuarioCampo(int idCampo) {
        
        final String sql = 
                "SELECT u.id, u.nombre, u.email \n"
                + "FROM ap_campo_usuario_rh_puesto cup, usuario u, rh_puesto p, ap_campo c \n"
                + "WHERE cup.ap_campo  = ? \n"
                + " AND cup.usuario = u.id \n"
                + " AND cup.ap_campo = c.id \n"
                + " AND cup.rh_puesto = p.id \n"
                + " AND cup.eliminado = 'False' \n"
                + " AND c.eliminado = 'False' \n"
                + " AND p.eliminado = 'False' \n"
                + "ORDER BY u.nombre asc";

        final List<Object[]> list = 
                em.createNativeQuery(sql)
                        .setParameter(1, idCampo)
                        .getResultList();
        
        final List<UsuarioVO> voList = new ArrayList<UsuarioVO>();

        for (Object[] objeto : list) {
            voList.add(castUsuario(objeto));
        }
        
        return voList;

    }

    
    public String traerUsuarioJsonPorCampo(int idCampo) {
        List<Object[]> lista;
        
        Gson gson = new Gson();
        
        String sql = 
                "SELECT u.id, u.nombre \n"
                +" FROM ap_campo_usuario_rh_puesto cup, usuario u  \n"
                + "WHERE cup.ap_campo  = ? \n"
                + " AND cup.usuario = u.id \n"
                + " AND cup.eliminado = 'False' \n"
                + "ORDER BY u.nombre ASC";
        
        lista = 
                em.createNativeQuery(sql)
                        .setParameter(1, idCampo)
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
        
        return gson.toJson(a);
    }

    
    public void modificarUsuarioPuesto(String sesion, int idCampo, String idUser, int puesto) {
        CampoUsuarioPuestoVo c = findByUsuarioCampo(idCampo, idUser);
        ApCampoUsuarioRhPuesto cup = find(c.getIdCampoUsuarioPuesto());
        String ae = cup.toString();
        cup.setRhPuesto(rhPuestoRemote.find(puesto));
        cup.setModifico(new Usuario(sesion));
        cup.setFechaModifico(new Date());
        cup.setHoraModifico(new Date());
        edit(cup);        
    }

    /**
     *
     * @param gerencia
     * @param idCampo
     * @return
     */
    
    public List<UsuarioVO> traerUsurioGerenciaCampoMenosGerente(int gerencia, int idCampo) {
        
        final String sql = 
                "SELECT u.id, u.nombre, u.email \n"
                + "FROM ap_campo_usuario_rh_puesto cup \n"
                + " INNER JOIN usuario u ON cup.usuario = u.id \n"
                + "WHERE cup.gerencia = ?"
                + " AND  cup.ap_campo = ? "
                + " AND cup.usuario not in ( \n"
                + "     SELECT responsable FROM ap_campo_gerencia \n"
                + "     WHERE gerencia = ? " 
                + "         AND eliminado = 'False') \n"
                + "	    AND cup.eliminado = 'False' \n"
                + "	    AND u.eliminado = 'False' \n"
                + "ORDER BY cup.usuario ASC";
        
        final List<Object[]> list = 
                em.createNativeQuery(sql)
                        .setParameter(1, gerencia)
                        .setParameter(2, idCampo)
                        .setParameter(3, gerencia)
                        .getResultList();
        final List<UsuarioVO> voList = new ArrayList<>();

        for (Object[] objeto : list) {
            voList.add(castUsuario(objeto));
        }
        
        return voList;

    }
    
    /**
     *
     * @param idUsuario
     * @param idCampo
     * @return
     */
    
    public int traerGerenciaUsuarioCampo(String idUsuario, int idCampo) {
        int gerenciaId = 0;
        try {
            final String sqlG
                    = "   SELECT cup.gerencia \n"
                    + " FROM ap_campo_usuario_rh_puesto cup \n"
                    + " INNER JOIN usuario u ON cup.usuario = u.id \n"
                    + " WHERE cup.ap_campo = ? "
                    + " AND  cup.usuario = ? "
                    + " AND cup.eliminado = false \n"
                    + " AND u.eliminado = false \n"
                    + " ORDER BY cup.id ASC"
                    + " limit 1";

            final Object obj
                    = (Object) em.createNativeQuery(sqlG)
                            .setParameter(1, idCampo)
                            .setParameter(2, idUsuario)
                            .getSingleResult();
            if (obj != null) {
                gerenciaId = (Integer) obj;
            }

        } catch (Exception e) {
            LOGGER.fatal(this, "", e);
            gerenciaId = 0;
        }
        return gerenciaId;
    }
    
    
}
