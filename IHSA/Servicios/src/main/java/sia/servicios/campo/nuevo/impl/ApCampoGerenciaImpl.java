/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.campo.nuevo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.ApCampoGerencia;
import sia.modelo.Gerencia;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class ApCampoGerenciaImpl extends AbstractFacade<ApCampoGerencia>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public ApCampoGerenciaImpl() {
	super(ApCampoGerencia.class);
    }    
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;

    
    public void guardarCampoGerenciaResponsable(String sesion, String responsable, int idCampo, int idGerencia) {
	UtilLog4j.log.info(this, "ApCampoGerenciaImpl.guardar");
	List<ApCampoGerenciaVo> existente = findByCampoGerenciaResponsable(idCampo, idGerencia, responsable, false);
	ApCampoGerenciaVo existenteGerenciaCampo = findByCampoGerencia(idCampo, idGerencia, true);
	ApCampoGerencia apCampoGerencia = new ApCampoGerencia();
	if (existente == null || existente.isEmpty()) {
	    if (existenteGerenciaCampo == null) {
		UtilLog4j.log.info(this, sesion + " " + responsable + " " + idCampo + " " + idGerencia);
                //Antes de cargar de Busca la gerencia y se verifica que no este eliminada si se encuentra eliminada se reactiva.
                Gerencia g = gerenciaRemote.find(idGerencia);
                if(!g.isEliminado()){
                    g.setEliminado(Constantes.NO_ELIMINADO);
                    g.setModifico(new Usuario(sesion));
                    g.setFechaModifico(new Date());
                    g.setHoraModifico(new Date());
                    gerenciaRemote.edit(g);
                }
		apCampoGerencia.setApCampo(apCampoRemote.find(idCampo));
		apCampoGerencia.setGerencia(g);
		apCampoGerencia.setResponsable(usuarioRemote.buscarPorNombre(responsable));
		apCampoGerencia.setGenero(new Usuario(sesion));
		apCampoGerencia.setFechaGenero(new Date());
		apCampoGerencia.setHoraGenero(new Date());
		apCampoGerencia.setEliminado(Constantes.NO_ELIMINADO);
		apCampoGerencia.setVisible(Constantes.BOOLEAN_TRUE);
		create(apCampoGerencia);
		UtilLog4j.log.info(this, "Gerencia CREATED SUCCESSFULLY");
	    } else {
		UtilLog4j.log.info(this, "Ya existe un responsable para esa gerencia campo: " + apCampoGerencia);
	    }
	} else {
	    UtilLog4j.log.info(this, "Ya existe uno igual Campo Gerencia Responsable: " + apCampoGerencia);
	}

    }

    
    public boolean verificaRelacionCampoGerenciaResponsable(String usuario, int campo, int gerencia) {
	Query q = em.createNativeQuery("select count(cg.id) from ap_campo_gerencia cg where cg.ap_campo = " + campo
		+ " AND cg.responsable = '" + usuario + "'"
		+ " AND cg.gerencia = " + gerencia
		+ " AND cg.eliminado = 'False'");
	int total = ((Integer) q.getSingleResult());
	if (total > 0) {
	    return true;
	} else {
	    return false;
	}
    }

    
    public List<ApCampoGerenciaVo> buscarCampoGerencia(String idUser, int idCampo) {
	try {
	    List<ApCampoGerenciaVo> le = new ArrayList<ApCampoGerenciaVo>();
	    Query q = em.createNativeQuery("select cg.id, cg.responsable, cg.gerencia, cg.ap_campo from ap_campo_gerencia cg where cg.ap_campo = " + idCampo
		    + " AND cg.responsable = '" + idUser + "'"
		    + " AND cg.eliminado = 'False'");
	    List<Object[]> lo = q.getResultList();
	    for (Object[] objects : lo) {
		le.add(castReturnApCampoGerenciaVO(objects));
	    }
	    return le;
	} catch (Exception e) {
	    return null;
	}
    }

    private ApCampoGerenciaVo castReturnApCampoGerenciaVO(Object[] obj) {
	ApCampoGerenciaVo apCampoGerenciaVo = new ApCampoGerenciaVo();
	apCampoGerenciaVo.setId((Integer) obj[0]);
	apCampoGerenciaVo.setIdResponsable((String) obj[1]);
	apCampoGerenciaVo.setIdGerencia((Integer) obj[2]);
	apCampoGerenciaVo.setIdApCampo((Integer) obj[3]);
	return apCampoGerenciaVo;
    }

    
    public List<ApCampoGerenciaVo> findAllCampoGerenciaPorCampo(int idCampo) {
	List<Object[]> list;
	clearQuery();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT a.id, g.id, g.NOMBRE, u.id,  u.NOMBRE, a.VISIBLE"
                + " FROM AP_CAMPO_GERENCIA a"
                + " inner join gerencia g on a.gerencia = g.id"
                + " inner join usuario u on a.responsable = u.id"
                + " WHERE a.AP_CAMPO = ?"
                + " AND a.ELIMINADO = ?"
                + " AND g.ELIMINADO = ?"
                + " ORDER BY g.nombre asc");
	list = em.createNativeQuery(sb.toString())
                .setParameter(1, idCampo)
                .setParameter(2, Constantes.NO_ELIMINADO)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .getResultList(); 
	UtilLog4j.log.info(this, "query: " + query.toString());
	List<ApCampoGerenciaVo> voList = new ArrayList<ApCampoGerenciaVo>();
	for (Object[] objeto : list) {
	    voList.add(castApGerenciaCampo(objeto));
	}
	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " ApGerencias");
	return voList;
    }

    /**
     *
     * @param idCampo
     * @param idGerencia
     * @param eliminado
     * @return
     */
    
    public ApCampoGerenciaVo findByCampoGerencia(int idCampo, int idGerencia, boolean eliminado) {
        try{
            UtilLog4j.log.info(this, "ApCampoGerenciaImpl.findByCampoGerencia()");

	ApCampoGerenciaVo apCampoGerenciaVo;

	clearQuery();
	Object[] objetos;

	query.append("SELECT a.ID, a.AP_CAMPO, a.GERENCIA, a.RESPONSABLE ");
	query.append(" FROM AP_CAMPO_GERENCIA a ");
	query.append(" WHERE a.AP_CAMPO=").append(idCampo);
	query.append(" AND a.GERENCIA=").append(idGerencia);
	query.append(" AND a.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("'");

	objetos = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	apCampoGerenciaVo = new ApCampoGerenciaVo();
	apCampoGerenciaVo.setId((Integer) objetos[0]);
	apCampoGerenciaVo.setIdApCampo((Integer) objetos[1]);
	apCampoGerenciaVo.setIdGerencia((Integer) objetos[2]);
	apCampoGerenciaVo.setIdResponsable((String) objetos[3]);

	return apCampoGerenciaVo;
        } catch(Exception e){
            UtilLog4j.log.error(this, e);
            return null;
        }

	
    }

    
    public List<ApCampoGerenciaVo> findByCampoGerenciaResponsable(int idCampo, int idGerencia, String responsable, boolean eliminado) {
	List<ApCampoGerenciaVo> lg = null;
	UtilLog4j.log.info(this, "ApCampoGerenciaImpl.findByCampoGerenciaResponsable()");
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(" SELECT a.AP_CAMPO, a.GERENCIA,a.RESPONSABLE ");
	    sb.append(" FROM AP_CAMPO_GERENCIA a ");
	    if (eliminado) {
		sb.append(" WHERE a.ELIMINADO='").append(Constantes.BOOLEAN_TRUE).append("' ");
	    } else {
		sb.append(" WHERE a.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
	    }
	    if (idCampo > 0) {
		sb.append(" and a.AP_CAMPO = ").append(idCampo);
	    }
	    if (idGerencia > 0) {
		sb.append(" and a.GERENCIA = ").append(idGerencia);
	    }

	    if (responsable != null && !responsable.isEmpty()) {
		sb.append(" and a.RESPONSABLE = '").append(responsable).append("'");
	    }

	    List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
	    if (lo != null) {
		lg = new ArrayList<ApCampoGerenciaVo>();
		ApCampoGerenciaVo apCampoGerenciaVo;
		for (Object[] objects : lo) {
		    apCampoGerenciaVo = new ApCampoGerenciaVo();
		    apCampoGerenciaVo.setIdApCampo((Integer) objects[0]);
		    apCampoGerenciaVo.setIdGerencia((Integer) objects[1]);
		    apCampoGerenciaVo.setNombreResponsable(String.valueOf(objects[2]));
		    lg.add(apCampoGerenciaVo);
		}
	    }
	} catch (Exception e) {
	    lg = null;
	}
	return lg;
    }
//a.id, g.id, g.NOMBRE, u.id,  u.NOMBRE, a.VISIBLE

    private ApCampoGerenciaVo castApGerenciaCampo(Object[] objeto) {
	ApCampoGerenciaVo vo = new ApCampoGerenciaVo();
	vo.setId((Integer) objeto[0]);
	vo.setIdGerencia((Integer) objeto[1]);
	vo.setNombreGerencia(String.valueOf(objeto[2]));
	vo.setIdResponsable(String.valueOf(objeto[3]));
	vo.setNombreResponsable(String.valueOf(objeto[4]));
	vo.setVisibleApCampo(Boolean.parseBoolean(String.valueOf(objeto[5])));
	vo.setSelected(false);
	return vo;
    }

    
    public void cambiarResponsable(String idSesion, int idApCampoGerencia, String responsable) {
	ApCampoGerencia apCampoGerencia = find(idApCampoGerencia);
	String ae = apCampoGerencia.toString();
	apCampoGerencia.setResponsable(new Usuario(responsable));
	apCampoGerencia.setModifico(new Usuario(idSesion));
	apCampoGerencia.setFechaModifico(new Date());
	apCampoGerencia.setHoraModifico(new Date());
	edit(apCampoGerencia);	
    }

    
    public UsuarioResponsableGerenciaVo buscarResponsablePorGerencia(int idGerencia, int idBloque) {
	clearQuery();
	try {
	    query.append("SELECT a.AP_CAMPO, a.GERENCIA,a.RESPONSABLE, u.nombre, u.email, g.nombre  FROM AP_CAMPO_GERENCIA a");
	    query.append("	inner join usuario u on a.responsable = u.id ");
	    query.append("	inner join gerencia g on a.gerencia = g.id ");
	    query.append(" WHERE a.AP_CAMPO = ").append(idBloque);
	    query.append(" AND  a.GERENCIA = ").append(idGerencia);
	    query.append(" AND a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    UsuarioResponsableGerenciaVo cg = null;
	    if (obj != null) {
		cg = new UsuarioResponsableGerenciaVo();
		cg.setIdApCampo((Integer) obj[0]);
		cg.setIdGerencia((Integer) obj[1]);
		cg.setIdUsuario((String) obj[2]);
		cg.setNombreUsuario((String) obj[3]);
		cg.setEmailUsuario((String) obj[4]);
		cg.setNombreGerencia((String) obj[5]);
	    }
	    return cg;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "No se encontrón relación gerencia-responsable " + e.getMessage());
	    return null;
	}
    }

    
    public void completarcambiarVisible(String idSesion, int idApCampoGerencia) {
	ApCampoGerencia apCampoGerencia = find(idApCampoGerencia);
	apCampoGerencia.setVisible(apCampoGerencia.isVisible() ? Boolean.FALSE :Boolean.TRUE);
	apCampoGerencia.setModifico(new Usuario(idSesion));
	apCampoGerencia.setFechaModifico(new Date());
	apCampoGerencia.setHoraModifico(new Date());
	edit(apCampoGerencia);	
    }

    
    public List<ApCampoGerenciaVo> listaGerentes(int idCampo) {
	clearQuery();
	query.append("select distinct(u.ID), u.NOMBRE from AP_CAMPO_GERENCIA cg");
	query.append(" inner join USUARIO u on cg.RESPONSABLE = u.ID");
	query.append(" inner join GERENCIA g on cg.GERENCIA = g.ID");
	query.append(" where cg.AP_CAMPO = ").append(idCampo);
	query.append(" and cg.VISIBLE = '").append(Constantes.BOOLEAN_TRUE).append("'");
	query.append(" order by u.NOMBRE asc");
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	List<ApCampoGerenciaVo> lg = null;
	if (lo != null) {
	    lg = new ArrayList<ApCampoGerenciaVo>();
	    for (Object[] objects : lo) {
		ApCampoGerenciaVo g = new ApCampoGerenciaVo();
		g.setIdResponsable((String) objects[0]);
		g.setNombreResponsable((String) objects[1]);
		lg.add(g);
	    }
	}
	return lg;
    }
    
    
    public void deleteApCampoGerencia(int idApcampo,String user){
        try{
            ApCampoGerencia a = find(idApcampo);
        a.setEliminado(Constantes.ELIMINADO);
        a.setModifico(new Usuario(user));
        a.setFechaModifico(new Date());
        a.setHoraModifico(new Date());
        edit(a);
        } catch (Exception e){
            UtilLog4j.log.fatal(this, e);
        }
    }
        
    
    public  boolean findRelacionGerenciaCampo(int gerencia){
        boolean regresa= false;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT a.id, g.id, g.NOMBRE, u.id, u.NOMBRE, a.VISIBLE, a.AP_CAMPO "
                + "FROM AP_CAMPO_GERENCIA a "
                + " inner join gerencia g on a.gerencia = g.id "
                + " inner join usuario u on a.responsable = u.id "
                + "  WHERE a.GERENCIA = ? "
                + "   AND a.ELIMINADO = ? "
                + "   AND g.ELIMINADO = ? "
                + "    ORDER BY a.ap_campo asc");
        
       List<Object> list = em.createNativeQuery(sb.toString())
               .setParameter(1, gerencia)
               .setParameter(2, Constantes.NO_ELIMINADO)
               .setParameter(3, Constantes.NO_ELIMINADO)
               .getResultList();
       
       if(list != null  && list.size() > 0){
          regresa=Constantes.TRUE;   
       }    
        return regresa;
           
    }
    
    
    
    public String correoGerencia(int apCampoID, String gerenciaIds) {
        String emails = "";
        String sb = 
                  " select  COALESCE(array_to_string(array_agg(DISTINCT email), ', '), '') "
                + " from responsable_gerencia_campo_vw a "
                + " where ap_campo = "+apCampoID
                + " and gerencia in ("+gerenciaIds+") ";

        Object obj = em.createNativeQuery(sb)                
                .getSingleResult();

        if (obj != null) {
            emails = String.valueOf(obj);
        }
        return emails;
    }
    
}
