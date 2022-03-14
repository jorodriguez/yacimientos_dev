/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgOficinaAnalistaImpl extends AbstractFacade<SgOficinaAnalista> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    private StringBuilder bodyQuery = new StringBuilder();    
    private int idSiEventoCrear = 1;
    private int idSiEventoEliminar = 3;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgOficinaAnalistaImpl() {
	super(SgOficinaAnalista.class);
    }

    public void clearBodyQuery() {
	this.bodyQuery.delete(0, this.bodyQuery.length());
    }

    
    public void save(String idAnalista, int idSgOficina, String idUsuario) {

	SgOficinaAnalista sgOficinaAnalista = new SgOficinaAnalista();
	sgOficinaAnalista.setSgOficina(this.sgOficinaRemote.find(idSgOficina));
	sgOficinaAnalista.setAnalista(new Usuario(idAnalista));
	sgOficinaAnalista.setGenero(new Usuario(idUsuario));
	sgOficinaAnalista.setFechaGenero(new Date());
	sgOficinaAnalista.setHoraGenero(new Date());
	sgOficinaAnalista.setEliminado(Constantes.NO_ELIMINADO);
	sgOficinaAnalista.setPrincipal(Constantes.BOOLEAN_FALSE);

	create(sgOficinaAnalista);
	UtilLog4j.log.info(this, "SgOficinaAnalista CREATED SUCCESSFULLY");
    }

    
    public void delete(int idSgOficinaAnalista, String idUsuario) {

	SgOficinaAnalista sgOficinaAnalista = find(idSgOficinaAnalista);

	sgOficinaAnalista.setModifico(new Usuario(idUsuario));
	sgOficinaAnalista.setFechaModifico(new Date());
	sgOficinaAnalista.setHoraModifico(new Date());
	sgOficinaAnalista.setEliminado(Constantes.ELIMINADO);

	edit(sgOficinaAnalista);
	UtilLog4j.log.info(this, "SgOficinaAnalista DELETED SUCCESSFULLY");
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void guardarAnalistaOficina(SgOficina sgOficina, Usuario usuario, String analista, boolean eliminado) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.guardarAnalistaOficina()");

	SgOficinaAnalista sgOficinaAnalista = new SgOficinaAnalista();
	sgOficinaAnalista.setAnalista(usuarioRemote.buscarPorNombre(analista));
	sgOficinaAnalista.setSgOficina(sgOficina);
	sgOficinaAnalista.setGenero(usuario);
	sgOficinaAnalista.setFechaGenero(new Date());
	sgOficinaAnalista.setHoraGenero(new Date());
	sgOficinaAnalista.setEliminado(eliminado);
	sgOficinaAnalista.setPrincipal(Constantes.BOOLEAN_FALSE);

	super.create(sgOficinaAnalista);
	UtilLog4j.log.info(this, "SgOficinaAnalista CREATED SUCCESSFULLY");
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void modificarSgOficinaAnalista(SgOficinaAnalista sgOficinaAnalista, Usuario usuario, Usuario analista) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.modificarSgOficinaAnalista()");

	UtilLog4j.log.info(this, "analista: " + sgOficinaAnalista.getAnalista().getNombre());

	sgOficinaAnalista.setAnalista(usuarioRemote.buscarPorNombre(sgOficinaAnalista.getAnalista().getNombre()));
	sgOficinaAnalista.setAnalista(analista);
	sgOficinaAnalista.setModifico(usuario);
	sgOficinaAnalista.setFechaModifico(new Date());
	sgOficinaAnalista.setHoraModifico(new Date());

	super.edit(sgOficinaAnalista);
	UtilLog4j.log.info(this, "SgOficinaAnalista UPDATED SUCCESSFULLY");
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void eliminarOficinaAnalista(SgOficinaAnalista sgOficinaAnalista, Usuario usuario, boolean eliminado) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.eliminarOficinaAnalista()");

	sgOficinaAnalista.setModifico(usuario);
	sgOficinaAnalista.setFechaModifico(new Date());
	sgOficinaAnalista.setHoraModifico(new Date());
	sgOficinaAnalista.setEliminado(eliminado);

	super.edit(sgOficinaAnalista);
	UtilLog4j.log.info(this, "SgOficinaAnalista DELETED SUCCESSFULLY");
    }

    
    public List<SgOficinaAnalista> getOficinasByAnalistaAndStatus(Usuario analista, boolean status) throws Exception {
        UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.getOficinasByAnalista()");
        List<SgOficinaAnalista> oficinasByAnalistaList = null;

        if (status && analista != null) {
            try {
                oficinasByAnalistaList
                        = em.createQuery(
                                "SELECT ao FROM SgOficinaAnalista ao WHERE ao.eliminado = :estado " 
                                        + " AND ao.analista.id = :idAnalista " 
                                        + " AND ao.sgOficina.vistoBueno = :vobo " 
                                        + " ORDER BY ao.id ASC")
                                .setParameter("vobo", true)
                                .setParameter("estado", status)
                                .setParameter("idAnalista", analista.getId())
                                .getResultList();

                if (oficinasByAnalistaList == null || oficinasByAnalistaList.isEmpty()) {
                    UtilLog4j.log.info(this, "No se encontraron Oficinas para el Analista " + analista.getId());
                    oficinasByAnalistaList = Collections.emptyList();
                } else {
                    UtilLog4j.log.info(this, "Se encontraron " + oficinasByAnalistaList.size() + " oficinas para el Analista " + analista.getId());
                }

            } catch (Exception e) {
                UtilLog4j.log.info(this, e.getMessage(), e);
                oficinasByAnalistaList = Collections.emptyList();
            }
        }

        return oficinasByAnalistaList;
    }

    
    public List<SgOficinaAnalista> getAnalistasByOficinaAndStatus(int oficina, boolean status) {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.getAnalistasByOficina()");

	List<SgOficinaAnalista> analistasByOficinaList = new ArrayList<SgOficinaAnalista>();

	if (status && oficina > 0) {
	    try {
		analistasByOficinaList = 
                        em.createQuery("SELECT ao FROM SgOficinaAnalista ao WHERE ao.eliminado = :estado AND ao.sgOficina.id = :idOficina AND ao.sgOficina.vistoBueno = :vobo ORDER BY ao.id ASC")
                                .setParameter("vobo", true).setParameter("estado", status).setParameter("idOficina", oficina).getResultList();
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		return null;
	    }

	}
         return  analistasByOficinaList;
    }

    
    public List<SgOficinaAnalista> traerOficinaPorVistoBueno(boolean voBo) {
	return em.createQuery("Select o FROM SgOficinaAnalista o WHERE o.sgOficina.vistoBueno = :voBo AND o.sgOficina.eliminado = :eli"
		+ " ORDER BY o.id DESC").setParameter("voBo", voBo).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
    }

    
    public List<SgOficinaAnalista> traerOficinaActiva(boolean eliminado) {
	return em.createQuery("Select o FROM SgOficinaAnalista o WHERE o.eliminado = :eli ORDER BY o.id DESC").setParameter("eli", eliminado).getResultList();
    }

    /**
     * 02/diciembre/2013 Actualizacion de semaforo Joel rodriguez p se puso que
     * obtuviera el campo Principal para tomar el analista quien atendera los
     * viajes para la cadena de aprobacion
     *
     * @param idSgOficina
     * @param orderByField
     * @param sortAscending
     * @param eliminado
     * @return
     */
    
    public List<SgOficinaAnalistaVo> getAllSgOficinaAnalista(int idSgOficina, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.getAllSgOficinaAnalista()");
	clearBodyQuery();
        StringBuilder sb = new StringBuilder();

	sb.append("SELECT oa.ID," //0
	+" oa.ANALISTA AS ID_ANALISTA," //1
	+" oa.SG_OFICINA AS ID_SG_OFICINA," //2
	+" u.NOMBRE AS NOMBRE_ANALISTA," //3
	+" o.NOMBRE AS NOMBRE_SG_OFICINA, " //4
	+" u.EMAIL as email_analista," //5
	+" oa.principal as principal" //6 (Actualizacion de semaforo Joel rodriguez)
	+" FROM SG_OFICINA_ANALISTA oa, USUARIO u, SG_OFICINA o "
	+" WHERE oa.ANALISTA=u.ID"
	+" AND oa.SG_OFICINA=o.ID"
	+" AND u.ELIMINADO = ?"
	+" AND o.ELIMINADO = ?"
	+" AND oa.ELIMINADO = ?"); //eliminado
        if(idSgOficina > Constantes.CERO){
            sb.append(" AND oa.SG_OFICINA in (?) ");
        } else {
            sb.append(" AND oa.SG_OFICINA not in  (?) ");
        }
	if (orderByField != null && !orderByField.isEmpty()) {
	    if ("id".equals(orderByField)) {
		sb.append("ORDER BY oa.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	    } else if ("nombre".equals(orderByField)) {
		sb.append("ORDER BY u.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	    }
	}

	UtilLog4j.log.info(this, sb.toString());

	List<Object[]> result = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.NO_ELIMINADO)
                .setParameter(3, (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO))
                .setParameter(4, idSgOficina)
                .getResultList();
	List<SgOficinaAnalistaVo> list = new ArrayList<SgOficinaAnalistaVo>();
	SgOficinaAnalistaVo vo = null;

	for (Object[] objects : result) {
	    vo = new SgOficinaAnalistaVo();
	    vo.setId((Integer) objects[0]);
	    vo.setIdAnalista((String) objects[1]);
	    vo.setIdSgOficina((Integer) objects[2]);
	    vo.setNombreAnalista((String) objects[3]);
	    vo.setNombreSgOficina((String) objects[4]);
	    vo.setEmailAnalista((String) objects[5]);
	    vo.setPrincipal((objects[6]).equals(true));
	    UtilLog4j.log.info(this, "vvv" + vo.isPrincipal());
	    list.add(vo);
	}

	UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgOficinaAnalista para la SgOficina: " + idSgOficina);

	return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public SgOficinaAnalistaVo traerAnalistaPrincipalPorOficina(int idOficina) {
	List<SgOficinaAnalistaVo> oa = this.getAllSgOficinaAnalista(idOficina, "id", true, false);
	SgOficinaAnalistaVo voRetorno = null;
	if (oa != null && !oa.isEmpty()) {
	    for (SgOficinaAnalistaVo vo : oa) {
		if (vo.isPrincipal()) {
		    voRetorno = vo;
		}
	    }
	}

	return voRetorno;
    }

    
    public void marcarPrincipal(int idOficinaAnalista, int idOficina, String sesion) {
	// qutamos a los que tengan analista principal
	List<SgOficinaAnalistaVo> loa = getAllSgOficinaAnalista(idOficina, "nombte", true, false);
	for (SgOficinaAnalistaVo sgOficinaAnalistaVo : loa) {
	    if (sgOficinaAnalistaVo.isPrincipal()) {
		principal(sgOficinaAnalistaVo.getId(), sesion, Constantes.BOOLEAN_FALSE);
	    }
	}
	//
	principal(idOficinaAnalista, sesion, Constantes.BOOLEAN_TRUE);

    }

    private void principal(int idOficinaAnalista, String sesion, boolean principal) {
	// Agrega el nuevo
	SgOficinaAnalista sgOficinaAnalista = find(idOficinaAnalista);
	sgOficinaAnalista.setModifico(new Usuario(sesion));
	sgOficinaAnalista.setFechaModifico(new Date());
	sgOficinaAnalista.setHoraModifico(new Date());
	sgOficinaAnalista.setPrincipal(principal);

	edit(sgOficinaAnalista);
	UtilLog4j.log.info(this, "SgOficinaAnalista DELETED SUCCESSFULLY");
    }

    
    public List<SgOficinaAnalistaVo> traerOficinaPorAnalista(String sesion) {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.getAllSgOficinaAnalista()");
	clearBodyQuery();

	this.bodyQuery.append(" SELECT oa.ID, "); //0
	this.bodyQuery.append(" oa.ANALISTA AS ID_ANALISTA, "); //1
	this.bodyQuery.append(" oa.SG_OFICINA AS ID_SG_OFICINA, "); //2
	this.bodyQuery.append(" u.NOMBRE AS NOMBRE_ANALISTA, "); //3
	this.bodyQuery.append(" o.NOMBRE AS NOMBRE_SG_OFICINA, "); //4
	this.bodyQuery.append(" u.EMAIL as email_analista, "); //5
	this.bodyQuery.append(" oa.principal as principal "); //6 (Actualizacion de semaforo Joel rodriguez)
	this.bodyQuery.append(" FROM SG_OFICINA_ANALISTA oa, USUARIO u, SG_OFICINA o ");
	this.bodyQuery.append(" WHERE oa.ANALISTA=u.ID ");
	this.bodyQuery.append(" AND oa.SG_OFICINA=o.ID ");
	this.bodyQuery.append(" AND u.ELIMINADO='False' ");
	this.bodyQuery.append(" AND o.ELIMINADO='False' ");
	this.bodyQuery.append(" AND oa.ELIMINADO='False'");
	this.bodyQuery.append(" AND oa.ANALISTA = '").append(sesion).append("'");
	this.bodyQuery.append(" ORDER BY u.id asc ");

	UtilLog4j.log.info(this, bodyQuery.toString());

	List<Object[]> result = em.createNativeQuery(this.bodyQuery.toString()).getResultList();
	List<SgOficinaAnalistaVo> list = new ArrayList<SgOficinaAnalistaVo>();
	SgOficinaAnalistaVo vo = null;

	for (Object[] objects : result) {
	    vo = new SgOficinaAnalistaVo();
	    vo.setId((Integer) objects[0]);
	    vo.setIdAnalista((String) objects[1]);
	    vo.setIdSgOficina((Integer) objects[2]);
	    vo.setNombreAnalista((String) objects[3]);
	    vo.setNombreSgOficina((String) objects[4]);
	    vo.setEmailAnalista((String) objects[5]);
	    vo.setPrincipal((objects[6]).equals(true));
	    UtilLog4j.log.info(this, "vvv" + vo.isPrincipal());
	    list.add(vo);
	}

	return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public List<OficinaVO> traerOficina(String sesion) {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.getAllSgOficinaAnalista()");
	clearQuery();
	query.append(""
		+ "SELECT o.id, o.NOMBRE, p.NOMBRE, e.NOMBRE, c.NOMBRE, oa.ID, oa.ANALISTA AS ID_ANALISTA,"
		+ " oa.SG_OFICINA AS ID_SG_OFICINA, u.NOMBRE AS NOMBRE_ANALISTA, o.NOMBRE AS NOMBRE_SG_OFICINA,"
		+ " u.EMAIL as email_analista, oa.principal as principal "
		+ " from SG_OFICINA_ANALISTA oa"
		+ "	inner join SG_OFICINA   o on oa.sg_oficina = o.ID "
		+ "	inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID "
		+ "	inner join SI_PAIS      p on d.SI_PAIS = p.ID    "
		+ "	inner join SI_ESTADO    e on e.SI_PAIS = e.ID    "
		+ "	inner join SI_CIUDAD    c on d.SI_ESTADO = c.ID"
		+ "	inner join usuario      u on	oa.analista = u.id "
		+ " WHERE  oa.ANALISTA = '").append(sesion).append("' and o.visto_bueno = 'True' ");
	query.append(" ORDER BY u.id asc ");

	UtilLog4j.log.info(this, query.toString());

	List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();
	List<OficinaVO> list = new ArrayList<OficinaVO>();
	OficinaVO vo = null;

	for (Object[] objects : result) {
	    vo = new OficinaVO();

	    vo.setId((Integer) objects[0]);
	    vo.setNombre((String) objects[1]);
	    vo.setNombreSiPais((String) objects[2]);
	    vo.setNombreSiEstado((String) objects[3]);
	    vo.setNombreSiCiudad((String) objects[4]);
	    vo.setSgOficinaAnalistaVo(new SgOficinaAnalistaVo());
	    vo.getSgOficinaAnalistaVo().setId((Integer) objects[5]);
	    vo.getSgOficinaAnalistaVo().setIdAnalista((String) objects[6]);
	    vo.getSgOficinaAnalistaVo().setIdSgOficina((Integer) objects[7]);
	    vo.getSgOficinaAnalistaVo().setNombreAnalista((String) objects[8]);
	    vo.getSgOficinaAnalistaVo().setNombreSgOficina((String) objects[9]);
	    vo.getSgOficinaAnalistaVo().setEmailAnalista((String) objects[10]);
	    vo.getSgOficinaAnalistaVo().setPrincipal((objects[11]).equals(true));
	    list.add(vo);
	}

	return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }
    
    
    public List<String> getEmailAllAnalistaSGLByCampo(int idApCampo) {
	UtilLog4j.log.info(this, "SgOficinaAnalistaImpl.getAllSgOficinaAnalistaByCampo()");
	clearBodyQuery();
        StringBuilder sb = new StringBuilder();

	sb.append("SELECT DISTINCT oa.ANALISTA AS ID_ANALISTA,\n" +
"	 u.NOMBRE AS NOMBRE_ANALISTA,\n" +
"	 u.EMAIL as email_analista\n" +
"	 FROM SG_OFICINA_ANALISTA oa\n" +
"	 INNER join USUARIO u on u.id = oa.ANALISTA and u.ELIMINADO = ? \n" +
"	 INNER join SG_OFICINA o on o.id= oa.SG_OFICINA and o.ELIMINADO = ? \n" +
"	 WHERE oa.ELIMINADO = ? AND o.AP_CAMPO = ? ");
        
	

	UtilLog4j.log.info(this, sb.toString());

	List<Object[]> result = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.NO_ELIMINADO)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, idApCampo)
                .getResultList();
        
	List<String> list = new ArrayList<String>();

	for (Object[] objects : result) {
	    list.add(objects[2].toString());
	}
        

	UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgOficinaAnalista para la SgOficina: " + idApCampo);

	return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

}
