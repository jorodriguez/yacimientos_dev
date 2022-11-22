/*
 * GerenciaImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.modelo.Gerencia;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Stateless 
public class GerenciaImpl extends AbstractFacade<Gerencia>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //

    @Inject
    private UsuarioImpl usuarioRemote;
    
    @Inject
    DSLContext dbCtx;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public GerenciaImpl() {
	super(Gerencia.class);
    }

    
    public List<Gerencia> findAll() {
	return em.createQuery("select object(o) from Gerencia as o WHERE o.visible = :visible ORDER BY o.nombre ASC").setParameter("visible", true).getResultList();
    }

    
    public List<Usuario> getApruebanOrden(String rfcCompania) {
	return em.createQuery("SELECT g.responsable FROM Gerencia g WHERE g.compania.rfc = :rfcCompania AND g.tipoNombramiento.id =:tipoNombramiento "
		+ " ORDER BY g.nombre ASC").setParameter("rfcCompania", rfcCompania) //                 .setParameter("visible", true)
		.setParameter("tipoNombramiento", 2).getResultList();
    }

    
    public String buscarGerenciaUsuario(String idUser) {
	String u = null;
	List<Gerencia> lista = em.createQuery("SELECT f FROM Gerencia f WHERE f.responsable.id = :idUser AND f.compania.nombre = :n AND f.visible = :t").setParameter("t", true).setParameter("idUser", idUser).setParameter("n", "Iberoamericana de Hidrocarburos S.A. de C.V.").getResultList();
	for (Gerencia gerencia : lista) {
	    if (gerencia.getNombre().equals("Administración")) {
		u = gerencia.getNombre();
	    }
	    if (gerencia.getNombre().equals("Servicios Genrales y Logistica")) {
		u = gerencia.getNombre();
	    }
	}
	return u;
    }

    
    public List<GerenciaVo> traerGerenciaActiva(String emprsa, int campo) {
//        return em.createQuery("SELECT f FROM Gerencia f WHERE f.compania.rfc = :rfc AND f.visible = :t AND f.tipoNombramiento.id = :tipo ORDER BY f.nombre ASC")
//                .setParameter("rfc", "IHI070320FI3")
//                .setParameter("tipo", 3)
//                .setParameter("t", true)
//                .getResultList();
	return getAllGerenciaByApCompaniaAndApCampo(emprsa, campo, "nombre", true, true, false);
    }

    /**
     *
     * @param campo
     * @return
     */
    
    public List<GerenciaVo> traerGerenciaActivaPorCampo(int campo) {
	String sb = "select cg.id, g.id, g.nombre from GERENCIA g\n"
		+ "    inner join AP_CAMPO_GERENCIA cg on cg.GERENCIA = g.ID\n"
		+ " where cg.AP_CAMPO = " + campo
		+ " and g.ELIMINADO = 'False'\n"
		+ " and cg.ELIMINADO  ='False'\n"
		+ " order by g.NOMBRE";
	List<Object[]> lo = em.createNativeQuery(sb).getResultList();
	List<GerenciaVo> lg = new ArrayList<GerenciaVo>();
	for (Object[] lo1 : lo) {
	    GerenciaVo g = new GerenciaVo();
	    g.setIdTabla((Integer) lo1[0]);
	    g.setId((Integer) lo1[1]);
	    g.setNombre((String) lo1[2]);
	    lg.add(g);
	}
	return lg;
    }

    
    public List<GerenciaVo> traerTodasGerencia() {
	clearQuery();
	List<Object[]> list = null;
	List<GerenciaVo> lg = null;
	query.append("select g.id, g.nombre, g.abrev from gerencia g where g.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" order by g.nombre asc");
	list = em.createNativeQuery(query.toString()).getResultList();
	if (list != null) {
	    lg = new ArrayList<GerenciaVo>();
	    for (Object[] objects : list) {
		lg.add(castGerencia(objects));
	    }
	}

	return lg;
    }

    
    public boolean isUsuarioResponsableForAnyGerencia(int idApCampo, String idUsuarioResponsable, boolean eliminado) {
	List<Object[]> list = null;

	clearQuery();
	query.append("SELECT cg.responsable  FROM AP_CAMPO_GERENCIA cg ");
	query.append(" WHERE cg.responsable = '").append(idUsuarioResponsable).append("'");
	query.append(" AND cg.ap_campo = ").append(idApCampo);
	query.append(" AND  cg.eliminado = '").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("'");
	//System.out.println("Responsable de cualquier gerencia: " + query.toString());
	list = em.createNativeQuery(query.toString()).getResultList();

	//UtilLog4j.log.info(this, list != null ? "El Usuario: " + idUsuarioResponsable + " es responsable de " + list.size() + " Gerencias" : "El Usuario: " + idUsuarioResponsable + " no es responsable de ninguna Gerencia");
	//UtilLog4j.log.info(this, "El Usuario es Gerente?: " + ((list == null || list.isEmpty()) ? false : true));
	return ((list != null && !list.isEmpty()));
    }

    //Nuevos:
    
    public String buscarPorUsuario(String rfcCompania, int idApCampo, String idUsuarioResponsable) {

	String nombreGerencia = Constantes.VACIO;
	List<GerenciaVo> list = getAllGerenciaByApCompaniaAndApCampo(rfcCompania, idApCampo, rfcCompania, true, true, false);

	if (list != null) {
	    nombreGerencia = list.get(0).getNombreResponsable();
	}
//        List<Gerencia> lista = em.createQuery("SELECT f FROM Gerencia f WHERE f.responsable.id = :idUser AND f.compania.nombre = :n AND f.visible = :t")
//                .setParameter("t", true)
//                .setParameter("idUser", idUser)
//                .setParameter("n", "Iberoamericana de Hidrocarburos S.A. de C.V.")
//                .getResultList();
//        for (Gerencia gerencia : lista) {
//            u = gerencia.getResponsable().getNombre();
//        }

	return nombreGerencia;
    }

    
    public Gerencia findByNameAndCompania(String nombreGerencia, String rfcCompania, boolean eliminado) {
	Integer idGerencia = -1;
	Gerencia g = null;

	try {
	    Query q = em.createNativeQuery("SELECT g.id " //0
		    + "FROM AP_COMPANIA_GERENCIA cg, GERENCIA g "
		    + " WHERE cg.compania = '" + rfcCompania + "' "
		    + " AND cg.gerencia = g.id "
		    + " AND g.nombre = '" + nombreGerencia + "' "
		    + " AND cg.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' ");

	    idGerencia = (Integer) q.getSingleResult();

	} catch (NonUniqueResultException nre) {

	    UtilLog4j.log.debug(this, nre.getMessage());
	    return g;
	} catch (NoResultException nre) {
	    UtilLog4j.log.debug(this, nre.getMessage());
	    return g;
	}

	g = find(idGerencia);
	UtilLog4j.log.info(this, idGerencia < 0 ? "No se encontró la Gerencia " + idGerencia + " en la Compania: " + rfcCompania : "Se encontró la Gerencia: " + idGerencia + " en la Compania: " + rfcCompania);
	return g;
    }

    
    public Gerencia findByIdAndCompania(int idGerencia, String rfcCompania, boolean eliminado) {
	Integer idG = -1;
	Gerencia g = null;

	try {
	    Query q = em.createNativeQuery("SELECT g.id " //0
		    + "FROM AP_COMPANIA_GERENCIA cg, GERENCIA g "
		    + " WHERE cg.compania = '" + rfcCompania + "' "
		    + " AND cg.gerencia = g.id "
		    + " AND g.id = " + idGerencia
		    + " AND cg.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' ");

	    idG = (Integer) q.getSingleResult();

	} catch (NonUniqueResultException nre) {
	    UtilLog4j.log.debug(this, nre.getMessage());
	    return g;
	} catch (NoResultException nre) {
	    UtilLog4j.log.debug(this, nre.getMessage());
	    return g;
	}

	g = find(idG);
	return g;
    }

    
    public Usuario getResponsableByApCampoAndGerencia(int idApCampo, int idGerencia, boolean eliminado) {
	String idUsuario = null;
	Usuario u = null;

	try {
	    Query q = em.createNativeQuery("SELECT cg.id, cg.responsable " //0
		    + "FROM AP_CAMPO_GERENCIA cg "
		    + " WHERE cg.ap_campo = " + idApCampo + " "
		    + " AND cg.gerencia = " + idGerencia + " "
		    + " AND cg.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' ");
	    Object[] obj = (Object[]) q.getSingleResult();
	    idUsuario = (String) obj[1];
	    UtilLog4j.log.info(this, "Id user: " + idUsuario);
	    u = this.usuarioRemote.find(idUsuario);
	    UtilLog4j.log.debug(this, u.getId() + " es el Usuario responsable de la Gerencia: " + idGerencia + " del ApCampo: " + idApCampo);

	} catch (NonUniqueResultException nre) {
	    UtilLog4j.log.debug(this, "NonUniqueResultException");
	    UtilLog4j.log.debug(this, nre.getMessage());
	    return u;
	} catch (NoResultException nre) {
	    UtilLog4j.log.debug(this, "NoResultException");
	    UtilLog4j.log.debug(this, nre.getMessage());
	    return u;
	}
	return u;
    }

    
    public UsuarioResponsableGerenciaVo traerResponsablePorApCampoYGerencia(int idApCampo, int idGerencia) {
	UtilLog4j.log.debug(this, "GerenciaImpl.getResponsableByApCampoAndGerencia()");
	UsuarioResponsableGerenciaVo vo = null;
	try {
	    clearQuery();
	    appendQuery("select cg.ID, "); //0
	    appendQuery(" cg.RESPONSABLE as id_usuario,"); //1
	    appendQuery(" u.NOMBRE as nombre_usuario,"); //2
	    appendQuery(" u.EMAIL as email_usuario,"); //3
	    appendQuery(" cg.AP_CAMPO as id_ap_campo,");  //4
	    appendQuery(" c.NOMBRE as nombre_ap_campo,"); //5
	    appendQuery(" cg.GERENCIA as id_gerencia,"); //6
	    appendQuery(" g.NOMBRE as nombre_gerencia"); //7
	    appendQuery(" from AP_CAMPO_GERENCIA cg, GERENCIA g, AP_CAMPO c, USUARIO u");
	    appendQuery(" where cg.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    appendQuery(" and cg.AP_CAMPO = ").append(idApCampo);
	    appendQuery(" and cg.GERENCIA = ").append(idGerencia);
	    appendQuery(" and g.ID = cg.GERENCIA ");
	    appendQuery(" and g.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    appendQuery(" and c.ID = cg.AP_CAMPO");
	    appendQuery(" and c.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    appendQuery(" and u.ID = cg.RESPONSABLE");
	    appendQuery(" and u.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");

	    UtilLog4j.log.debug(this, query.toString());

	    Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

	    if (objects != null) {
		vo = new UsuarioResponsableGerenciaVo();
		vo.setId((Integer) objects[0]);
		vo.setIdUsuario((String) objects[1]);
		vo.setNombreUsuario((String) objects[2]);
		vo.setEmailUsuario((String) objects[3]);
		vo.setIdApCampo((Integer) objects[4]);
		vo.setNombreApCampo((String) objects[5]);
		vo.setIdGerencia((Integer) objects[6]);
		vo.setNombreGerencia((String) objects[7]);
	    }
	    return vo;
	} catch (NoResultException nre) {
	    UtilLog4j.log.error(this, ("GerenciaImpl.traerResponsablePorApCampoYGerencia()" + nre.getMessage()), nre);
	} catch (Exception e) {
	    UtilLog4j.log.error(this, ("GerenciaImpl.traerResponsablePorApCampoYGerencia()" + e.getMessage()), e);
	}
	return vo;
    }

    
    public List<GerenciaVo> findAll(String orderByField, boolean sortAscending, boolean eliminado) {
	List<Object[]> list;

	Query q = em.createNativeQuery("SELECT g.id, " //0
		+ " g.nombre AS nombre_gerencia " //1
		+ " FROM GERENCIA g "
		+ " WHERE g.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' "
		+ " ORDER BY g." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
	list = q.getResultList();
	List<GerenciaVo> voList = new ArrayList<GerenciaVo>();
	GerenciaVo vo;
	for (Object[] objects : list) {
	    vo = new GerenciaVo();
	    vo.setId((Integer) objects[0]);
	    vo.setNombre((String) objects[1]);
	    voList.add(vo);
	}
	UtilLog4j.log.debug(this, "Se encontraron " + (list != null ? list.size() : 0) + " Gerencias");

	return (voList != null ? voList : Collections.EMPTY_LIST);
    }

    
    public List<GerenciaVo> getAllGerenciaByApCampo(int idApCampo, String orderByField, boolean sortAscending, Boolean visible, boolean eliminado) {
	List<Object[]> list;
	String comodin = Constantes.VACIO;
	if (visible != null) {
	    comodin = " AND cg.visible = '" + (visible ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE) + "' ";
	}

	Query q = em.createNativeQuery("SELECT cg.id, " //0
		+ "g.id AS id_gerencia, " //1
		+ "g.nombre AS nombre_gerencia, " //2
		+ "c.id AS id_ap_campo, " //3
		+ "c.nombre AS nombre_ap_campo, " //4
		+ "u.id AS id_responsable, " //5
		+ "u.nombre AS nombre_responsable " //6
		+ "FROM AP_CAMPO_GERENCIA cg, GERENCIA g, AP_CAMPO c, Usuario u "
		+ "WHERE cg.AP_CAMPO = " + idApCampo + " "
		+ "AND cg.AP_CAMPO = c.id "
		+ "AND cg.GERENCIA = g.id "
		+ "AND cg.RESPONSABLE = u.id "
		+ comodin
		+ "AND cg.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' "
		+ "ORDER BY g." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
	list = q.getResultList();
	List<GerenciaVo> voList = new ArrayList<GerenciaVo>();
	GerenciaVo vo;
	for (Object[] objects : list) {
	    vo = new GerenciaVo();
	    vo.setId((Integer) objects[1]);
	    vo.setNombre((String) objects[2]);
	    vo.setIdApCampo((Integer) objects[3]);
	    vo.setNombreApCampo((String) objects[4]);
	    vo.setIdResponsable((String) objects[5]);
	    vo.setNombreResponsable((String) objects[6]);
	    voList.add(vo);
	}
	return (voList != null ? voList : Collections.EMPTY_LIST);
    }

    
    
    public  List<GerenciaVo> getAllGerenciasByCampo() {
        List<GerenciaVo> retVal = new ArrayList<>();
        
        try {
            retVal = 
                    (List<GerenciaVo>) dbCtx.fetch(
                            "SELECT c.id as id_ap_campo, \n" +
                            "   c.nombre AS nombre_ap_campo,\n" +
                            "   u.id AS id_responsable,\n" +
                            "   u.nombre AS nombre_responsable,\n" +
                            "   g.nombre AS nombre, \n" +
                            "   g.id,\n" +
                            "   com.nombre as nombre_compania, \n" +
                            "   com.rfc as rfc_Compania\n" +        
                            "FROM ap_campo_gerencia cg\n" +
                            "	INNER JOIN gerencia g ON cg.gerencia = g.id and g.eliminado = false\n" +
                            "	INNER JOIN ap_campo c ON cg.ap_campo = c.id and c.eliminado = false" +
                            "	INNER JOIN usuario u ON cg.responsable = u.id\n" +
                            "   INNER join compania com on com.rfc = c.compania and com.eliminado= false" +
                            "   WHERE cg.eliminado = false \n" +
                            "ORDER BY com.nombre,c.nombre, g.nombre"
                    ).into(GerenciaVo.class);
        } catch (Exception e) {
            UtilLog4j.log.warn(this, "", e);
        }
        
        return retVal;
    }
    
    
    
    public List<GerenciaVo> getAllGerenciaByApCompania(String rfcCompania, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.debug(this, "GerenciaImpl.getAllGerenciaByApCompania()");

	List<Object[]> list;

	Query q = em.createNativeQuery("SELECT cg.id, " //0
		+ "g.id AS id_gerencia, " //1
		+ "g.nombre AS nombre_gerencia, " //2
		+ "c.rfc AS id_compania, " //3
		+ "c.nombre AS nombre_compania " //4
		+ "FROM AP_COMPANIA_GERENCIA cg, GERENCIA g, COMPANIA c "
		+ "WHERE cg.COMPANIA = '" + rfcCompania + "' "
		+ "AND cg.COMPANIA = c.rfc "
		+ "AND cg.GERENCIA = g.id "
		+ "AND cg.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' "
		+ "ORDER BY g." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
	list = q.getResultList();
	List<GerenciaVo> voList = new ArrayList<GerenciaVo>();
	GerenciaVo vo;
	for (Object[] objects : list) {
	    vo = new GerenciaVo();
	    vo.setId((Integer) objects[1]);
	    vo.setNombre((String) objects[2]);
	    vo.setRfcCompania((String) objects[3]);
	    vo.setNombreCompania((String) objects[4]);
	    voList.add(vo);
	}
	return (voList != null ? voList : Collections.EMPTY_LIST);
    }

    
    public List<GerenciaVo> getAllGerenciaByApCompaniaAndApCampo(String rfcCompania, int idApCampo, String orderByField, boolean sortAscending, Boolean visible, boolean eliminado) {
	try {
	    List<Object[]> list;
	    StringBuilder comodin = new StringBuilder();
	    if (visible != null) {
		comodin.append(" AND cg.visible = '").append(visible.booleanValue() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("' ");
	    }
	    clearQuery();
	    query.append("SELECT cg.id, "); //0
	    query.append(" g.id AS id_gerencia, "); //1
	    query.append(" g.nombre AS nombre_gerencia, "); //2
	    query.append(" c.rfc AS id_compania, "); //3
	    query.append(" c.nombre AS nombre_compania "); //4
	    query.append(" FROM AP_COMPANIA_GERENCIA cg, GERENCIA g, COMPANIA c ");
	    query.append(" WHERE cg.compania = '").append(rfcCompania).append("' ");
	    query.append(" AND cg.compania = c.rfc ");
	    query.append(" AND cg.gerencia = g.id ");
	    query.append(" AND cg.eliminado = '").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");
	    query.append(" AND g.id IN ");
	    query.append(" (SELECT ");
	    query.append(" g.id AS id ");//1
	    query.append(" FROM AP_CAMPO_GERENCIA cg, GERENCIA g, AP_CAMPO c, Usuario u ");
	    query.append(" WHERE cg.AP_CAMPO = ").append(idApCampo).append(" ");
	    query.append(" AND cg.AP_CAMPO = c.id ");
	    query.append(" AND cg.GERENCIA = g.id ");
	    query.append(" AND cg.RESPONSABLE = u.id ");
	    query.append(" AND cg.eliminado = '").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");
	    query.append(comodin.toString());
	    query.append(" ) ");
	    query.append(" ORDER BY g.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	    list = em.createNativeQuery(query.toString()).getResultList();

	    List<GerenciaVo> voList = new ArrayList<GerenciaVo>();
	    GerenciaVo vo;
	    for (Object[] objects : list) {
		vo = new GerenciaVo();
		vo.setId((Integer) objects[1]);
		vo.setNombre((String) objects[2]);
		vo.setRfcCompania((String) objects[3]);
		vo.setNombreCompania((String) objects[4]);
		voList.add(vo);
	    }

	    return (voList != null ? voList : Collections.EMPTY_LIST);
	} catch (Exception e) {
	    UtilLog4j.log.debug(this, "Excepcion al hacer la consulta de gerenicas " + e.getMessage());
	    return null;
	}
    }

    
    public List<GerenciaVo> traerGerenciaPorCompaniaCampo(String rfcCompania, int idApCampo, boolean eliminado) {
	try {
	    List<Object[]> list;
	    clearQuery();
	    query.append("SELECT cg.id, "); //0
	    query.append(" g.id AS id_gerencia, "); //1
	    query.append(" g.nombre AS nombre_gerencia, "); //2
	    query.append(" c.rfc AS id_compania, "); //3
	    query.append(" c.nombre AS nombre_compania "); //4
	    query.append(" FROM AP_COMPANIA_GERENCIA cg, GERENCIA g, COMPANIA c ");
	    query.append(" WHERE cg.compania = '").append(rfcCompania).append("' ");
	    query.append(" AND cg.compania = c.rfc ");
	    query.append(" AND cg.gerencia = g.id ");
	    query.append(" AND cg.eliminado = '").append(eliminado).append("' ");
	    query.append(" ORDER BY g.nombre asc ");
	    list = em.createNativeQuery(query.toString()).getResultList();

	    List<GerenciaVo> voList = new ArrayList<GerenciaVo>();
	    GerenciaVo vo;
	    for (Object[] objects : list) {
		vo = new GerenciaVo();
		vo.setId((Integer) objects[1]);
		vo.setNombre((String) objects[2]);
		vo.setRfcCompania((String) objects[3]);
		vo.setNombreCompania((String) objects[4]);
		voList.add(vo);
	    }
	    return (voList != null ? voList : Collections.EMPTY_LIST);

	} catch (Exception e) {
	    UtilLog4j.log.debug(this, "Excepcion al hacer la consulta de gerenicas " + e.getMessage());
	    return null;
	}
    }

    
    public List<GerenciaVo> getAllGerenciaByApCampoAndResponsable(int idApCampo, String idUsuarioResponsable, String orderByField, boolean sortAscending, Boolean visible, boolean eliminado) {
	List<Object[]> list;
	String comodin = Constantes.VACIO;
	if (visible != null) {
	    comodin = " AND cg.visible = '" + (visible ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE) + "' ";
	} else {
	    UtilLog4j.log.info(this, "Trae todos los registros ...");
	}

	Query q = em.createNativeQuery("SELECT cg.id, " //0
		+ "g.id AS id_gerencia, " //1
		+ "g.nombre AS nombre_gerencia, " //2
		+ "c.id AS id_ap_campo, " //3
		+ "c.nombre AS nombre_ap_campo, " //4
		+ "u.id AS id_responsable, " //5
		+ "u.nombre AS nombre_responsable " //6
		+ "FROM AP_CAMPO_GERENCIA cg, GERENCIA g, AP_CAMPO c, Usuario u "
		+ "WHERE cg.RESPONSABLE = '" + idUsuarioResponsable + "' "
		+ "AND cg.AP_CAMPO = " + idApCampo + " "
		+ "AND cg.AP_CAMPO = c.id "
		+ "AND cg.GERENCIA = g.id "
		+ "AND cg.RESPONSABLE = u.id "
		+ comodin
		+ " AND cg.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' "
		+ "ORDER BY g." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
	list = q.getResultList();

	List<GerenciaVo> voList = new ArrayList<GerenciaVo>();
	GerenciaVo vo;
	for (Object[] objects : list) {
	    vo = new GerenciaVo();
	    vo.setId((Integer) objects[1]);
	    vo.setNombre((String) objects[2]);
	    vo.setIdApCampo((Integer) objects[3]);
	    vo.setNombreApCampo((String) objects[4]);
	    vo.setIdResponsable((String) objects[5]);
	    vo.setNombreResponsable((String) objects[6]);
	    voList.add(vo);
	}
	return (voList != null ? voList : Collections.EMPTY_LIST);
    }

    
    public List<GerenciaVo> getAllGerenciaByResponsable(String idUsuarioResponsable, String orderByField, boolean sortAscending, Boolean visible, boolean eliminado) {
	String comodin = Constantes.VACIO;
	List<Object[]> list;
	if (visible != null) {
	    comodin = " AND cg.visible = '" + (visible ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE) + "' ";
	}

	Query q = em.createNativeQuery("SELECT cg.id, " //0
		+ "g.id AS id_gerencia, " //1
		+ "g.nombre AS nombre_gerencia, " //2
		+ "c.id AS id_ap_campo, " //3
		+ "c.nombre AS nombre_ap_campo, " //4
		+ "u.id AS id_responsable, " //5
		+ "u.nombre AS nombre_responsable " //6
		+ "FROM AP_CAMPO_GERENCIA cg, GERENCIA g, AP_CAMPO c, Usuario u "
		+ "WHERE cg.RESPONSABLE = '" + idUsuarioResponsable + "' "
		+ "AND cg.AP_CAMPO = c.id "
		+ "AND cg.GERENCIA = g.id "
		+ "AND cg.RESPONSABLE = u.id "
		+ comodin
		+ "AND cg.eliminado = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "' "
		+ "ORDER BY cg." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
	list = q.getResultList();
	List<GerenciaVo> voList = new ArrayList<GerenciaVo>();
	GerenciaVo vo;
	for (Object[] objects : list) {
	    vo = new GerenciaVo();
	    vo.setId((Integer) objects[1]);
	    vo.setNombre((String) objects[2]);
	    vo.setIdApCampo((Integer) objects[3]);
	    vo.setNombreApCampo((String) objects[4]);
	    vo.setIdResponsable((String) objects[5]);
	    vo.setNombreResponsable((String) objects[6]);
	    voList.add(vo);
	}
	return (voList != null ? voList : Collections.EMPTY_LIST);
    }


    /*
     * Metodo que recibe un id de gerencia y un id de campo Retorna : Nombre del
     * responsable de la Gerencia, Puesto que tienen el responsable, Nombre de
     * la gerencia
     *
     * Atributos que recoje: IdUsuario es el id del responsable} Nombre : es el
     * nombre del usuario * Puesto : es el nombre del puesto idGerencia: id de
     * la gerencia Gerencia: es el nombre de la gerencia
     *
     */
    
    public UsuarioVO findDetailGerencia(Integer idGerencia, Integer idApCampo) {
	UtilLog4j.log.info(this, "findDetailGerencia");
	UsuarioVO vo;
	String q = Constantes.VACIO;
	try {
	    q = " SELECT cg.responsable as Id_Responsable," //0
		    + "     us.NOMBRE as Nombre_Responsable,"//1
		    + "     (Select rhp.NOMBRE FROM RH_PUESTO rhp,AP_CAMPO_USUARIO_RH_PUESTO cup "
		    + "                                             Where cup.AP_CAMPO =  " + idApCampo
		    + "                                             AND cup.USUARIO = cg.RESPONSABLE "
		    + "                                             AND cup.RH_PUESTO = rhp.id "
		    + "                                             AND rhp.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
		    + "                                             AND cup.ELIMINADO='False')"//
		    + "     AS Puesto,"//2
		    + "     camp.NOMBRE AS Campo,"//3
		    + "     ge.id AS Id_Gerencia,"//4
		    + "     ge.NOMBRE AS Gerencia, "//5
		    + "     us.EMAIL"//6
		    + " From AP_CAMPO_GERENCIA cg, gerencia ge,AP_CAMPO camp,USUARIO us"
		    + " Where cg.AP_CAMPO = " + idApCampo
		    + "      AND cg.AP_CAMPO = camp.ID"
		    + "      AND cg.GERENCIA = " + idGerencia
		    + "      AND cg.GERENCIA = ge.id"
		    + "      AND cg.RESPONSABLE = us.ID "
		    + "      AND ge.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'";

	    Query query = em.createNativeQuery(q);
	    Object[] obj = (Object[]) query.getSingleResult();

	    vo = new UsuarioVO();
	    vo.setId((String) obj[0]);
	    vo.setNombre((String) obj[1]);
	    vo.setPuesto((String) obj[2]);
	    vo.setCampo((String) obj[3]);
	    vo.setIdGerencia((Integer) obj[4]);
	    vo.setGerencia((String) obj[5]);
	    vo.setMail((String) obj[6]);
	    return vo;

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer detalle de la gerencia " + e.getMessage());
	    return null;
	}
    }

    
    public void guardarGerencia(String session, String nombre) throws ExistingItemException {
	UtilLog4j.log.debug(this, "Aún falta implementar este método!!!!!!!!!!!!!");
//    UtilLog4j.log.debug(this, "Entrando a Guardar nombre de gerencia");
//    GerenciaVo existente = findByName(nombre, false);
//         gerencia = new Gerencia();
//        if (existente == null) {
//            gerencia.setNombre(nombre);
//            gerencia.setEliminado(Constantes.BOOLEAN_FALSE);
//            gerencia.setFechaGenero(new Date());
//            gerencia.setHoraGenero(new Date());
//            gerencia.setGenero(usuarioRemote.find(session));
//            gerencia.setVisible(Constantes.BOOLEAN_TRUE);
//            UtilLog4j.log.info(this, "Valor de gerencia: " + gerencia.toString());
//            create(gerencia);
//            UtilLog4j.log.info(this, "Gerencia CREATED SUCCESSFULLY");
//            UtilLog4j.log.debug(this, "Valor de gerencia: " + gerencia.toString());
//            create(gerencia);
//            UtilLog4j.log.debug(this, "Gerencia CREATED SUCCESSFULLY");
//
//        } else {
//            throw new ExistingItemException("gerencia.mensaje.error.siGerenciaExistente", gerencia.getNombre(), gerencia);
//        }
    }

    public GerenciaVo findByName(String nombre, boolean eliminado) {
	try {
	    Object[] objetos;
	    GerenciaVo gerenciaVo; //para el valor de retorno
	    Query q = em.createNativeQuery("SELECT a.NOMBRE,a.ID"
		    + " FROM GERENCIA a"
		    + " WHERE a.NOMBRE='"
		    + nombre + "'"
		    + " AND a.ELIMINADO='"
		    + Constantes.NO_ELIMINADO + "'");
	    //guardar los objetos del query en el arreglo
	    objetos = (Object[]) q.getSingleResult();
	    //validacion de lo que trae de la db
	    gerenciaVo = new GerenciaVo();
	    gerenciaVo.setNombre(String.valueOf(objetos[0]));
	    gerenciaVo.setId((Integer) objetos[1]);
	    return gerenciaVo;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString());
	    return null;
	}
    }

    private GerenciaVo castGerencia(Object[] objects) {
	GerenciaVo gerenciaVo = new GerenciaVo();
	gerenciaVo.setId((Integer) objects[0]);
	gerenciaVo.setNombre((String) objects[1]);
	gerenciaVo.setAbrev((String) objects[2]);
	return gerenciaVo;
    }

    
    public List<GerenciaVo> traerGerenciaAbreviatura(int idCampo) {
	List<Object[]> list = null;
	clearQuery();
	try {
	    query.append(" select  cg.GERENCIA, g.NOMBRE, g.abrev from AP_CAMPO_GERENCIA cg ");
	    query.append(" inner join GERENCIA g on cg.GERENCIA = g.ID and cg.AP_CAMPO = ").append(idCampo).append(" and cg.VISIBLE = '").append(Constantes.BOOLEAN_TRUE).append("'  and cg.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("' ");
	    query.append(" where  cg.GERENCIA in (select g.ID from GERENCIA g where g.ABREV is not null and g.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("')");
	    list = em.createNativeQuery(query.toString()).getResultList();
	    List<GerenciaVo> lg = null;
	    if (list != null) {
		lg = new ArrayList<GerenciaVo>();
		for (Object[] obj : list) {
		    lg.add(castGerencia(obj));
		}
	    }
	    return lg;
	} catch (Exception e) {
	    UtilLog4j.log.debug(this, "e: " + e.getMessage());
	    return null;
	}
    }
    
    
    public List<SelectItem> traerGerenciaAbreviaturaItems(int idCampo) {
	List<Object[]> list = null;
	clearQuery();
	try {
	    query.append(" select  cg.GERENCIA, g.NOMBRE, g.abrev from AP_CAMPO_GERENCIA cg ");
	    query.append(" inner join GERENCIA g on cg.GERENCIA = g.ID and cg.AP_CAMPO = ").append(idCampo).append(" and cg.VISIBLE = '").append(Constantes.BOOLEAN_TRUE).append("'  and cg.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("' ");
	    query.append(" where  cg.GERENCIA in (select g.ID from GERENCIA g where g.ABREV is not null and g.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("')");
	    list = em.createNativeQuery(query.toString()).getResultList();
	    List<SelectItem> lg = null;
	    if (list != null) {
		lg = new ArrayList<SelectItem>();
		for (Object[] obj : list) {
                    SelectItem item = new SelectItem((Integer) obj[0], (String) obj[1]);                    
		    lg.add(item);
		}
	    }
	    return lg;
	} catch (Exception e) {
	    UtilLog4j.log.debug(this, "e: " + e.getMessage());
	    return null;
	}
    }

    
    public GerenciaVo traerGerenciaVOAbreviatura(String abrevGerencia) {
	clearQuery();
	try {
	    query.append(" select g.id, g.NOMBRE, g.abrev from GERENCIA g ");
	    query.append(" where  g.ABREV = '").append(abrevGerencia).append("'");
	    Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    GerenciaVo lg = null;
	    if (obj != null) {
		lg = castGerencia(obj);
	    }
	    return lg;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "e: " + e.getMessage());
	    return null;
	}
    }

    
    public List<GerenciaVo> traerGerenciaVoSecundariaAbreviatura(String abrevGerencia) {
	clearQuery();
	List<GerenciaVo> lg = null;
	try {
	    query.append(" select g.id, g.NOMBRE, g.abrev from GERENCIA g ");
	    query.append(" where  g.ABREV like '").append(abrevGerencia).append("%'");
	    List<Object[]> obj = em.createNativeQuery(query.toString()).getResultList();
	    if (obj != null) {
		lg = new ArrayList<GerenciaVo>();
		for (Object[] object : obj) {
		    lg.add(castGerencia(object));
		}
	    }
	    return lg;
	} catch (Exception e) {
	    UtilLog4j.log.debug(this, "e: " + e.getMessage());
	}
	return lg;
    }

    
    public GerenciaVo buscarPorId(int idGerencia) {
	clearQuery();
	try {
	    query.append(" select g.id, g.NOMBRE, g.ABREV from GERENCIA g ");
	    query.append(" where  g.id = ").append(idGerencia);
	    Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    GerenciaVo lg = null;
	    if (obj != null) {
		lg = castGerencia(obj);
	    }
	    return lg;
	} catch (NoResultException e) {
	    UtilLog4j.log.debug(this, "e: " + e.getMessage());
	}
	return null;
    }

    
    public void deleteGerencia(int idGerencia, String user) {
	try {
	    Gerencia g = find(idGerencia);
	    g.setEliminado(Constantes.ELIMINADO);
	    g.setModifico(new Usuario(user));
	    g.setFechaModifico(new Date());
	    g.setHoraModifico(new Date());
	    edit(g);
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e);
	}

    }
}
