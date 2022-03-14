/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.modelo.*;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiPaisImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgLicenciaImpl extends AbstractFacade<SgLicencia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private DSLContext dslCtx;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgLicenciaImpl() {
        super(SgLicencia.class);
    }
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SiPaisImpl siPaisRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    public String queryBase() {
        return "select l.id, l.numero,u.nombre, p.nombre, l.fecha_expedida, l.fecha_vencimiento, l.SI_ADJUNTO, "
                + " te.nombre , l.vigente,   l.si_pais,   l.sg_tipo_especifico, adj.uuid, u.id "
                + " from SG_LICENCIA l"
                + " inner join USUARIO u on l.USUARIO = u.ID and u.eliminado = ? and u.activo = ? "
                + " inner join SI_PAIS p on l.SI_PAIS = p.ID"
                + " inner join SG_TIPO_ESPECIFICO te on l.SG_TIPO_ESPECIFICO = te.ID"
                + " inner join ap_campo_usuario_rh_puesto a on a.usuario = u.id and a.eliminado = ?"
                + " left join SI_ADJUNTO adj on l.SI_ADJUNTO = adj.ID";
    }

    
    public List<LicenciaVo> traerLicienciaPorUsuario(String idUsuario) {
        try {
            Query q = em.createNativeQuery("select "
                    + " l.id,"
                    + " l.numero,"
                    + " u.nombre, "
                    + " p.nombre, "
                    + " l.fecha_expedida, "
                    + " l.fecha_vencimiento, "
                    + "  CASE WHEN l.si_adjunto is null THEN 0 "
                    + "   WHEN l.si_adjunto is not null THEN l.si_adjunto "
                    + "   END, "
                    + " te.nombre , "
                    + " l.vigente,   "
                    + " l.si_pais,   "
                    + " l.sg_tipo_especifico ,  "
                    + " CASE WHEN l.si_adjunto is null THEN ''"
                    + " WHEN l.si_adjunto is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = l.si_adjunto)   "
                    + " END AS uuid_adjunto,"
                    + " u.id"
                    + " from sg_licencia l, usuario u, si_pais p, sg_tipo_especifico te"
                    + " where l.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " and l.usuario = '" + idUsuario + "'"
                    + " and l.usuario =u.id  "
                    + " and l.si_pais = p.id"
                    + " and l.sg_tipo_especifico = te.id "
                    + " order by l.vigente desc");
            UtilLog4j.log.info(this, "Licencias por User: " + q.toString());
            List<LicenciaVo> li = new ArrayList<LicenciaVo>();
            List<Object[]> obj = q.getResultList();
            for (Object[] objects : obj) {
                li.add(castLicenciaVo(objects));
            }
            return li;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exc; " + e.getMessage());
            return null;
        }
    }
    //Traer todas las licencias vigentes

    
    public List<LicenciaVo> traerLiciencia(int campo, String tipo) {
        String sql
                = "SELECT l.id, l.numero, l.fecha_expedida AS expedida, l.fecha_vencimiento AS vencimiento\n"
                + "	, COALESCE(l.si_adjunto, 0) AS adjunto\n"
                + "	, u.id AS id_usuario, u.nombre AS usuario, p.nombre AS pais\n"
                + "	, te.nombre AS tipo, l.vigente\n"
                + "	, l.si_pais AS id_pais\n"
                + "	, l.sg_tipo_especifico AS id_tipo\n"
                + "	, COALESCE(adj.uuid, '') AS uuid\n"
                + "FROM sg_licencia l\n"
                + "	INNER JOIN usuario u ON l.usuario = u.id\n"
                + "	INNER JOIN si_pais p ON l.si_pais = p.id\n"
                + "	INNER JOIN sg_tipo_especifico te ON l.sg_tipo_especifico = te.id\n"
                + "     INNER JOIN ap_campo_usuario_rh_puesto ar on ar.usuario = u.id \n"
                + "	LEFT JOIN si_adjunto adj ON l.si_adjunto = adj.id\n"
                + "WHERE  \n"
                + "	l.eliminado = 'false'\n"
                + "	AND ar.ap_campo = ?\n"
                + "	AND u.eliminado = 'False'\n"
                + "	AND u.activo = 'True'\n"
                + tipo
                + " ORDER BY u.nombre ASC";

        List<LicenciaVo> retVal = null;

        try {
            retVal = dslCtx.fetch(sql, campo).into(LicenciaVo.class);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "SQL: " + sql, e);
//            return null;
        }

        return retVal;
    }

    private LicenciaVo castLicenciaVo(Object[] obj) {
        LicenciaVo licenciaVo = new LicenciaVo();
        licenciaVo.setId((Integer) obj[0]);
        licenciaVo.setNumero((String) obj[1]);
        licenciaVo.setNombre((String) obj[2]);
        licenciaVo.setPais((String) obj[3]);
        licenciaVo.setExpedida((Date) obj[4]);
        licenciaVo.setVencimiento((Date) obj[5]);
        licenciaVo.setAdjunto((Integer) obj[6] != null ? (Integer) obj[6] : 0);
        licenciaVo.setTipo((String) obj[7]);
        licenciaVo.setVigente((boolean) obj[8]);
        licenciaVo.setIdPais((Integer) obj[9]);
        licenciaVo.setIdTipo((Integer) obj[10]);
        licenciaVo.setUuid((String) obj[11] != null ? (String) obj[11] : "");
        licenciaVo.setIdUsuario((String) obj[12]);
        return licenciaVo;
    }

    
    public boolean quitarLicenciaVigente(Usuario usuario, int idLicencia) {
        boolean v;
        try {
            SgLicencia licencia = find(idLicencia);
            licencia.setGenero(usuario);
            licencia.setFechaGenero(new Date());
            licencia.setHoraGenero(new Date());
            licencia.setVigente(Constantes.BOOLEAN_FALSE);
            edit(licencia);

            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    
    public void guardarLicencia(Usuario usuario, LicenciaVo licenciaVo, String user, int idTipoEspecifico, SgTipo sgTipo, int idPais) {
        try {
            SgLicencia sgLicencia = new SgLicencia();
            sgLicencia.setNumero(licenciaVo.getNumero());
            sgLicencia.setSgTipo(sgTipo);
            sgLicencia.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idTipoEspecifico));
            sgLicencia.setUsuario(usuarioRemote.buscarPorNombre(user));
            sgLicencia.setSiPais(siPaisRemote.find(idPais));
            sgLicencia.setSiAdjunto(null);
            sgLicencia.setFechaExpedida(licenciaVo.getExpedida());
            sgLicencia.setFechaVencimiento(licenciaVo.getVencimiento());
            sgLicencia.setGenero(usuario);
            sgLicencia.setFechaGenero(new Date());
            sgLicencia.setHoraGenero(new Date());
            sgLicencia.setEliminado(Constantes.NO_ELIMINADO);
            sgLicencia.setVigente(Constantes.BOOLEAN_TRUE);
            create(sgLicencia);
            //Genera pone el tipo especifico como usudo

        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public boolean agregarArchivoLicencia(int idLicencia, Usuario usuario, SiAdjunto siAdjunto) {
        boolean v;
        try {
            SgLicencia sgLicencia = find(idLicencia);
            sgLicencia.setGenero(usuario);
            sgLicencia.setSiAdjunto(siAdjunto);
            sgLicencia.setFechaGenero(new Date());
            sgLicencia.setHoraGenero(new Date());
            edit(sgLicencia);

            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    
    public boolean quitarArchivoLicencia(int idLicencia, Usuario usuario) {
        boolean v;
        try {
            SgLicencia sgLicencia = find(idLicencia);
            sgLicencia.setGenero(usuario);
            sgLicencia.setFechaGenero(new Date());
            sgLicencia.setHoraGenero(new Date());
            sgLicencia.setSiAdjunto(null);
            edit(sgLicencia);

            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            v = false;
        }
        return v;
    }

    
    public void modificarLicencia(Usuario usuario, int idLicencia, int idTipoEspecifico, int idPais, Date inicio, Date vencimiento, String numero) {
        try {
            SgLicencia sgLicencia = find(idLicencia);
            sgLicencia.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idTipoEspecifico));
            sgLicencia.setSiPais(siPaisRemote.find(idPais));
            sgLicencia.setFechaExpedida(inicio);
            sgLicencia.setNumero(numero);
            sgLicencia.setFechaVencimiento(vencimiento);
            sgLicencia.setGenero(usuario);
            sgLicencia.setFechaGenero(new Date());
            sgLicencia.setHoraGenero(new Date());
            edit(sgLicencia);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public void eliminarLicencia(Usuario usuario, int idLicencia) {
        try {
            SgLicencia sgLicencia = find(idLicencia);
            sgLicencia.setUsuario(usuario);
            sgLicencia.setGenero(usuario);
            sgLicencia.setFechaGenero(new Date());
            sgLicencia.setHoraGenero(new Date());
            sgLicencia.setEliminado(Constantes.ELIMINADO);
            sgLicencia.setVigente(Constantes.BOOLEAN_FALSE);
            edit(sgLicencia);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public List<LicenciaVo> traerLicienciaVigente(boolean semamna, int apcampo, String order) {
        try {
            UtilLog4j.log.info("traerLicienciaVigente()");
            StringBuilder sb = new StringBuilder();
            sb.append("select l.id, l.numero, l.FECHA_EXPEDIDA, l.FECHA_VENCIMIENTO,"//0-3
                    + " case when l.si_adjunto is not null then l.si_adjunto when l.si_adjunto is null then 0 end, u.id, u.nombre, "//4-6
                    + " pa.id, pa.nombre, te.id, te.nombre,o.id, o.NOMBRE, a.ID, a.NOMBRE, u.gerencia "//7-15
                    + " from sg_licencia l"
                    + " inner join SI_PAIS pa on l.SI_PAIS = pa.ID "
                    + " inner join SG_TIPO_ESPECIFICO te on l.SG_TIPO_ESPECIFICO = te.ID "
                    + " inner join USUARIO u on l.USUARIO = u.ID AND U.ELIMINADO = ?"
                    + " INNER JOIN SG_OFICINA o on o.id = u.SG_OFICINA and o.ELIMINADO = ?"
                    + " inner join AP_CAMPO a on a.id = o.AP_CAMPO and a.ELIMINADO = ?"//1
                    + " Where l.eliminado = ? and  o.AP_CAMPO = ? and");//2

            if (semamna) {
                String fecha = Constantes.FMT_yyyy_MM_dd.format(siManejoFechaLocal.fechaSumarMes(new Date(), Constantes.MESES_PREVIOS));
                sb.append(" l.FECHA_VENCIMIENTO BETWEEN CURRENT_DATE and '")
                        .append(fecha).append("'");
            } else {
                sb.append(" l.fecha_vencimiento < CURRENT_DATE  ");
            }

            sb.append(order);

            List<Object[]> lo = em.createNativeQuery(sb.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.NO_ELIMINADO)
                    .setParameter(3, Constantes.NO_ELIMINADO)
                    .setParameter(4, Constantes.NO_ELIMINADO)
                    .setParameter(5, apcampo)
                    .getResultList();
            List<LicenciaVo> lvo = new ArrayList<LicenciaVo>();

            for (Object[] objects : lo) {
                lvo.add(castLicenciaVO(objects));
            }
            return lvo;

        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return null;

        }
    }

    
    public LicenciaVo buscarLicenciaVigentePorUsuario(String idUsuario) {
        String sql
                = "SELECT l.id, l.numero, l.fecha_expedida AS expedida, l.fecha_vencimiento AS vencimiento\n"
                + "	, COALESCE(l.si_adjunto, 0) AS adjunto\n"
                + "	, u.id AS id_usuario, u.nombre AS usuario, pa.id AS id_pais, pa.nombre AS pais\n"
                + "	, te.id AS id_tipo, te.nombre AS tipo\n"
                + "	, o.id AS id_oficina, o.nombre as nombre_oficina\n"
                + "	, a.id AS id_ap_campo, a.nombre AS nombre_ap_campo\n"
                + "FROM sg_licencia l\n"
                + "	INNER JOIN si_pais pa ON l.si_pais = pa.id\n"
                + "	INNER JOIN sg_tipo_especifico te ON l.sg_tipo_especifico = te.id\n"
                + "	INNER JOIN usuario u ON l.usuario = u.ID AND u.eliminado = ?\n"
                + "	INNER JOIN sg_oficina o ON o.id = u.sg_oficina AND o.eliminado = ?\n"
                + "	INNER JOIN ap_campo a ON a.id = o.ap_campo AND a.eliminado = ?\n"
                + "WHERE l.usuario = ?\n"
                + "	AND l.vigente = ?\n"
                + "	AND l.eliminado = ?";

        LicenciaVo retVal = null;

        try {

            Record record
                    = dslCtx.fetchOne(sql,
                            Constantes.NO_ELIMINADO, Constantes.NO_ELIMINADO, Constantes.NO_ELIMINADO,
                            idUsuario,
                            Constantes.BOOLEAN_TRUE, Constantes.NO_ELIMINADO
                    );

            if (record != null) {
                retVal = record.into(LicenciaVo.class);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "SQL : " + sql, e);
        }

        return retVal;
    }

    private LicenciaVo castLicenciaVO(Object[] obj) {
        LicenciaVo licenciaVo = new LicenciaVo();
        licenciaVo.setId((Integer) obj[0]);
        licenciaVo.setNumero((String) obj[1]);
        licenciaVo.setExpedida((Date) obj[2]);
        licenciaVo.setVencimiento((Date) obj[3]);
        licenciaVo.setAdjunto((Integer) obj[4]);
        licenciaVo.setIdUsuario((String) obj[5]);
        licenciaVo.setUsuario((String) obj[6]);
        licenciaVo.setIdPais((Integer) obj[7]);
        licenciaVo.setPais((String) obj[8]);
        licenciaVo.setIdTipo((Integer) obj[9]);
        licenciaVo.setTipo((String) obj[10]);
        licenciaVo.setIdOficina((Integer) obj[11]);
        licenciaVo.setNombreOficina((String) obj[12]);
        licenciaVo.setIdApCampo((Integer) obj[13]);
        licenciaVo.setNombreApCampo((String) obj[14]);
        licenciaVo.setGerencia((Integer) obj[15]);
        return licenciaVo;
    }

    
    public List<LicenciaVo> traerLicenciasPorOficina(int oficina, Date fecha15DiasAntesDeVencimiento) {
        UtilLog4j.log.info(this, "## TraerLicencias " + oficina + " ##");
        UtilLog4j.log.info(this, "## Fecha" + fecha15DiasAntesDeVencimiento + " ##");
        clearQuery();
        List<LicenciaVo> ll = new ArrayList<LicenciaVo>();
        String fecha = Constantes.FMT_ddMMyyy.format(fecha15DiasAntesDeVencimiento);
        try {
            appendQuery("select l.id, l.numero, l.FECHA_EXPEDIDA, l.FECHA_VENCIMIENTO,\n"
                    + "            case when l.si_adjunto is not null then l.si_adjunto when l.si_adjunto is null then 0 end\n"
                    + "            , u.id, u.nombre, pa.id, pa.nombre, te.id, te.nombre, o.id, o.NOMBRE, a.ID, a.NOMBRE, u.gerencia \n"
                    + "            from SG_LICENCIA l \n"
                    + "            inner join SI_PAIS pa on l.SI_PAIS = pa.ID \n"
                    + "            inner join SG_TIPO_ESPECIFICO te on l.SG_TIPO_ESPECIFICO = te.ID \n"
                    + "            inner join USUARIO u on l.USUARIO = u.ID AND U.ELIMINADO = ? \n"
                    +//1
                    "            inner join SG_OFICINA o on o.id = u.SG_OFICINA and o.ELIMINADO = ? \n"
                    + //2
                    "            inner join AP_CAMPO a on a.id = o.AP_CAMPO and a.ELIMINADO = ?\n"
                    +//3
                    "            where l.FECHA_VENCIMIENTO = ? \n"
                    + //4
                    "            and l.USUARIO in (select av.USUARIO from SG_ASIGNAR_VEHICULO av where av.PERTENECE = ? \n"
                    + //5
                    "            and av.TERMINADA = ?\n"
                    +//6
                    "            and av.SG_VEHICULO in (select v.id from SG_VEHICULO v where v.SG_OFICINA = ?))\n"
                    +//7
                    "            and l.ELIMINADO = ?");//8
            List<Object[]> lo = em.createNativeQuery(query.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.NO_ELIMINADO)
                    .setParameter(3, Constantes.NO_ELIMINADO)
                    .setParameter(4, fecha15DiasAntesDeVencimiento)
                    .setParameter(5, Constantes.CERO)
                    .setParameter(6, Constantes.BOOLEAN_FALSE)
                    .setParameter(7, oficina)
                    .setParameter(8, Constantes.NO_ELIMINADO)
                    .getResultList();

            if (lo != null) {
                for (Object[] objects : lo) {
                    ll.add(castLicenciaVO(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existio una excepcion en traerLicencieas por oficina" + e.getMessage());
        }
        return ll;
    }

    
    public boolean buscarPaisUsado(SiPais siPais) {
        boolean v = false;
        List<SgLicencia> ll = em.createQuery("Select l FROM SgLicencia l WHERE l.siPais.id = :pais AND l.eliminado = :eli").setParameter("pais", siPais.getId()).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        if (!ll.isEmpty()) {
            v = true;
        }
        return v;
    }

    
    public List<LicenciaVo> traerLicienciaByFechas(String tipo, Date fechaInicio, Date fecchaFin, int apCampo) {
        try {

            clearQuery();
            query.append(queryBase());
            query.append(" where 1=1"
                    + " and  l.ELIMINADO = ?"
                    + " and l.fecha_vencimiento BETWEEN ? and ?"
                    + " and a.ap_campo = ?");

            query.append(tipo
                    + " order by u.NOMBRE asc");

            List<LicenciaVo> li = new ArrayList<LicenciaVo>();
            List<Object[]> obj = em.createNativeQuery(query.toString())
                    .setParameter(1, Constantes.BOOLEAN_FALSE)
                    .setParameter(2, Constantes.BOOLEAN_TRUE)
                    .setParameter(3, Constantes.BOOLEAN_FALSE)
                    .setParameter(4, Constantes.BOOLEAN_FALSE)
                    .setParameter(5, fechaInicio)
                    .setParameter(6, fecchaFin)
                    .setParameter(7, apCampo)
                    .getResultList();
            UtilLog4j.log.info(this, "Licencias sin User: " + query.toString());
            for (Object[] objects : obj) {
                li.add(castLicenciaVo(objects));
            }
            return li;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exc; " + e.getMessage());
            return null;
        }
    }

    
    public List<LicenciaVo> traerLicienciaByfechaMenor(String tipo, Date fecha, int apCampo) {
        try {

            String sql
                    = "SELECT l.id, l.numero, l.fecha_expedida AS expedida, l.fecha_vencimiento AS vencimiento\n"
                    + "	, COALESCE(l.si_adjunto, 0) AS adjunto\n"
                    + "	, u.id AS id_usuario, u.nombre AS usuario, p.nombre AS pais\n"
                    + "	, te.nombre AS tipo, l.vigente\n"
                    + "	, l.si_pais AS id_pais\n"
                    + "	, l.sg_tipo_especifico AS id_tipo\n"
                    + "	, COALESCE(adj.uuid, '') AS uuid\n"
                    + "FROM sg_licencia l\n"
                    + "	INNER JOIN usuario u ON l.usuario = u.id\n"
                    + "	INNER JOIN si_pais p ON l.si_pais = p.id\n"
                    + "	INNER JOIN sg_tipo_especifico te ON l.sg_tipo_especifico = te.id\n"
                    + "	LEFT JOIN si_adjunto adj ON l.si_adjunto = adj.id\n"
                    + "WHERE  1=1 \n"
                    + " and  l.ELIMINADO = ?"
                    + " and l.fecha_vencimiento <= ? :: date"
                    + " and u.ap_campo = ?"
                    + tipo
                    + " order by u.NOMBRE asc";

            List<LicenciaVo> li = new ArrayList<>();

            li = dslCtx.fetch(sql, Constantes.BOOLEAN_FALSE, Constantes.FMT_yyyyMMdd.format(fecha), apCampo).into(LicenciaVo.class);

            return li;
        } catch (DataAccessException e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    
    public List<LicenciaVo> traerLicienciaByfechaMayor(String tipo, Date fecha, int apCampo) {
        try {

            
            String sql
                    = "SELECT l.id, l.numero, l.fecha_expedida AS expedida, l.fecha_vencimiento AS vencimiento\n"
                    + "	, COALESCE(l.si_adjunto, 0) AS adjunto\n"
                    + "	, u.id AS id_usuario, u.nombre AS usuario, p.nombre AS pais\n"
                    + "	, te.nombre AS tipo, l.vigente\n"
                    + "	, l.si_pais AS id_pais\n"
                    + "	, l.sg_tipo_especifico AS id_tipo\n"
                    + "	, COALESCE(adj.uuid, '') AS uuid\n"
                    + "FROM sg_licencia l\n"
                    + "	INNER JOIN usuario u ON l.usuario = u.id\n"
                    + "	INNER JOIN si_pais p ON l.si_pais = p.id\n"
                    + "	INNER JOIN sg_tipo_especifico te ON l.sg_tipo_especifico = te.id\n"
                    + "	LEFT JOIN si_adjunto adj ON l.si_adjunto = adj.id\n"
                    + "WHERE  1=1 \n"
                    + " and  l.ELIMINADO = ?"
                    + " and l.fecha_vencimiento >= ? :: date"
                    + " and u.ap_campo = ?"
                    + tipo
                    + " order by u.NOMBRE asc";

            List<LicenciaVo> li = new ArrayList<>();

            li = dslCtx.fetch(sql, Constantes.BOOLEAN_FALSE, Constantes.FMT_yyyyMMdd.format(fecha), apCampo).into(LicenciaVo.class);
            return li;
        } catch (DataAccessException e) {
            UtilLog4j.log.fatal(this, "Exc; " + e);
            return null;
        }
    }
}
