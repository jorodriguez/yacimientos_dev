/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.usuario.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.EmailNotFoundException;
import sia.modelo.CoNoticia;
import sia.modelo.RhCampoGerencia;
import sia.modelo.RhUsuarioGerencia;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.gerencia.vo.RhTipoGerenciaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioGerenciaVo;
import sia.notificaciones.usuario.impl.ServicioNotificacionUsuarioImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.comunicacion.impl.CoNoticiaUsuarioImpl;
import sia.servicios.rh.impl.RhCampoGerenciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class RhUsuarioGerenciaImpl extends AbstractFacade<RhUsuarioGerencia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private ServicioNotificacionUsuarioImpl notificacionUsuarioRemote;
    @Inject
    private CoNoticiaUsuarioImpl coNoticiaUsuarioRemote;
    @Inject
    private CoNoticiaImpl coNoticiaRemote;
    @Inject
    private RhCampoGerenciaImpl rhCampoGerenciaRemote;    
    @Inject
    private RhTipoGerenciaImpl rhTipoGerenciaRemote;
    private StringBuilder bodyQuery = new StringBuilder();
    
    

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public RhUsuarioGerenciaImpl() {
	super(RhUsuarioGerencia.class);
    }

    
    public boolean iniciarBajaEmpleado(String usuarioBaja, String motivoBaja, List<GerenciaVo> listaGerenciasLiberar, String usuarioRealizo) {
	log("iniciarBajaEmpleado");
	RhUsuarioGerencia rhUsuarioGerencia = null;
	boolean todoBien = true;
	List<RhTipoGerenciaVo> listaCompartidos = new ArrayList<RhTipoGerenciaVo>();
	try {	    
	    if (listaGerenciasLiberar.size() > 0) {
		for (GerenciaVo vo : listaGerenciasLiberar) {
		    log("Enviando correo para el resposanble " + vo.getNombreResponsable());
		    RhCampoGerencia rhCampoGerencia = rhCampoGerenciaRemote.buscarCampoGerencia(vo.getIdApCampo(), vo.getId());
		    if (rhCampoGerencia != null) {
			if (notificacionUsuarioRemote.notificationBajaUsuario(vo.getId(), rhCampoGerencia.getId(), vo.getIdResponsable(), usuarioBaja, usuarioRealizo)) {
			    todoBien = true;
			}
		    } else {
			log("NO SE ENCONTRÓ UNA RELACION ENTRE CAMPO " + vo.getIdApCampo() + " y " + vo.getIdResponsable());
			return false;
		    }
		}
		//guardar los datos
		for (GerenciaVo vo : listaGerenciasLiberar) {
		    RhCampoGerencia rhCampoGerencia = rhCampoGerenciaRemote.buscarCampoGerencia(vo.getIdApCampo(), vo.getId());
		    if (rhCampoGerencia != null) {
			rhUsuarioGerencia = new RhUsuarioGerencia();
			rhUsuarioGerencia.setRhCampoGerencia(rhCampoGerencia);
			//log("RH CAMPO GERENCIA   " + rhUsuarioGerencia.getRhCampoGerencia().getGerencia().getNombre());
			rhUsuarioGerencia.setLiberado(Constantes.BOOLEAN_FALSE);
			rhUsuarioGerencia.setUsuario(new Usuario(usuarioBaja));
			rhUsuarioGerencia.setGenero(new Usuario(usuarioRealizo));
			rhUsuarioGerencia.setFechaGenero(new Date());
			rhUsuarioGerencia.setHoraGenero(new Date());
			rhUsuarioGerencia.setEliminado(Constantes.BOOLEAN_FALSE);

			create(rhUsuarioGerencia);

			List<RhTipoGerenciaVo> l = rhTipoGerenciaRemote.findAllRhTipoGerenciaByRhCampoGerencia(rhCampoGerencia.getId(), "id", true, false);
			if (l != null) {
			    listaCompartidos.addAll(l);
			    log("Usuario agregados desde rhCampoGerencia " + l.size());
			}
			log("Agregada la gerencia " + vo.getNombre());
		    }
		}
		usuarioRemote.guardarMotivoBaja(usuarioBaja, motivoBaja, usuarioRealizo);
		//publicar noticia
		createNoticia(listaGerenciasLiberar, listaCompartidos, usuarioBaja, usuarioRealizo);
	    }
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    log("Excepcion al dar de baja el empleado " + e.getMessage());
	    return false;
	}
    }

    private void createNoticia(List<GerenciaVo> listaGerenciasCompartir, List<RhTipoGerenciaVo> list, String usuarioBaja, String idUsuario) {
	log("crearNoticia" + idUsuario);
	String titulo = "";
	StringBuilder mensaje = new StringBuilder();

	List<ComparteCon> listComparteCon = new ArrayList<ComparteCon>();
	try {
	    titulo = "Separación laboral de empleado " + usuarioRemote.find(usuarioBaja).getNombre();

	    mensaje.append("<p>Estamos en proceso de la separación laboral del empleado <strong>").append(usuarioRemote.find(usuarioBaja).getNombre()).append("</strong>");
	    mensaje.append(" Por favor confirmar si hay algún adeudo del personal mencionado con sus respectivos departamentos.</p>");
	    listComparteCon = castGerentesToComparteCon(listaGerenciasCompartir);
	    //Compartir con los detalles de tipoGerencia
	    listComparteCon.addAll(castRhTipoGerenciaToComparteCon(list));

	    //quien creo el viaje
	    listComparteCon.add(new ComparteCon(idUsuario, "", "", "Usuario"));
	    listComparteCon.add(new ComparteCon("SIA", "", "", "Usuario"));

	    if (listComparteCon != null && !listComparteCon.isEmpty()) {
		log("crear noticia");
		CoNoticia noti = coNoticiaRemote.nuevaNoticia("SIA", titulo, "", mensaje.toString(), 0, 0, listComparteCon);
		if (noti != null) {
		    try {
			//logService.create(SgSolicitudViaje.class.getName(), solicitud.getId(), eventoService.find(2), idUsuario, antesEvento, solicitud.toString());
			coNoticiaUsuarioRemote.crearRelacionNoticiaUsuario(usuarioBaja, noti.getId(), 23, usuarioBaja);
			log("Se publico la NOTICIA de la solicitud de viaje");
		    } catch (Exception ex) {
			log("Excepcion al crear la       noticia " + ex.getMessage());
		    }
		}
	    }
	} catch (Exception e) {
	    log("Excepcion al crear la noticia " + e.getMessage());
	}
    }

    private List<ComparteCon> castGerentesToComparteCon(List<GerenciaVo> listaGerencias) {
	log("castGerentesToComparteCon");
	Usuario gerente = null;
	ComparteCon compateCon = null;
	try {
	    List<ComparteCon> listaCompartidos = new ArrayList<ComparteCon>();
	    for (GerenciaVo vo : listaGerencias) {
		gerente = usuarioRemote.find(vo.getIdResponsable());
		compateCon = new ComparteCon();
		compateCon.setId(gerente.getId());
		compateCon.setCorreoUsuario(gerente.getEmail());
		compateCon.setNombre(gerente.getNombre());
		compateCon.setTipo("Usuario");
		listaCompartidos.add(compateCon);
		log(gerente.getNombre() + "Compartido  ");

	    }
	    return listaCompartidos;
	} catch (Exception e) {
	    log("Excepcion al castear los gerentes compartidos " + e.getMessage());
	    return null;
	}
    }

    private List<ComparteCon> castRhTipoGerenciaToComparteCon(List<RhTipoGerenciaVo> listaRhTipoGerenciaVo) {
	ComparteCon compateCon = null;
	try {
	    List<ComparteCon> listaCompartidos = new ArrayList<ComparteCon>();
	    for (RhTipoGerenciaVo t : listaRhTipoGerenciaVo) {
		Usuario u = usuarioRemote.find(t.getIdUsuario());
		if (u != null) {
		    compateCon = new ComparteCon();
		    compateCon.setId(u.getId());
		    compateCon.setCorreoUsuario(u.getEmail());
		    compateCon.setNombre(u.getNombre());
		    compateCon.setTipo("Usuario");
		    listaCompartidos.add(compateCon);
		    log(u.getNombre() + "Compartido  ");
		}
	    }
	    return listaCompartidos;
	} catch (Exception e) {
	    log("Excepcion al castear los usuario  " + e.getMessage());
	    return null;
	}
    }

    public void clearBodyQuery() {
	this.bodyQuery.delete(0, this.bodyQuery.length());
    }

//    
//    public List<UsuarioGerenciaVo> findAll(int idGerencia, int idRhCampoGerencia, String responsableLibera, Boolean liberado, String orderByField, boolean sortAscending) {
//        clearBodyQuery();
//        this.bodyQuery.append("SELECT ug.ID, "); //0
//        this.bodyQuery.append("u.ID AS ID_USUARIO, "); //1
//        this.bodyQuery.append("u.NOMBRE AS NOMBRE_USUARIO, "); //2
//        this.bodyQuery.append("c.ID AS ID_AP_CAMPO, "); //3
//        this.bodyQuery.append("c.NOMBRE AS NOMBRE_AP_CAMPO, "); //4
//        this.bodyQuery.append("g.ID AS ID_GERENCIA, "); //5
//        this.bodyQuery.append("g.NOMBRE AS NOMBRE_GERENCIA,"); //6
//        this.bodyQuery.append("nu.CO_NOTICIA, "); //7
//        this.bodyQuery.append("ug.USUARIO_LIBERO AS ID_USUARIO_LIBERO, "); //8
//        this.bodyQuery.append("(SELECT u2.NOMBRE FROM USUARIO u2 WHERE u2.ug.USUARIO_LIBERO) AS NOMBRE_USUARIO_LIBERO, "); //9
//        this.bodyQuery.append("ug.RH_CAMPO_GERENCIA AS ID_RH_CAMPO_GERENCIA "); //10
//        this.bodyQuery.append("FROM RH_USUARIO_GERENCIA ug, Usuario u, AP_CAMPO c, GERENCIA g, co_noticia_usuario nu ");
//        this.bodyQuery.append("WHERE ug.ELIMINADO='False' ");
//        if (idGerencia > 0) {
//            this.bodyQuery.append("AND ug.GERENCIA=").append(idGerencia).append(" ");
//        }
//        if (idRhCampoGerencia > 0) {
//            this.bodyQuery.append("AND ug.RH_CAMPO_GERENCIA=").append(idRhCampoGerencia).append(" ");
//        }
//        if (responsableLibera != null && !responsableLibera.isEmpty()) {
//            this.bodyQuery.append("AND ug.RESPONSABLE='").append(responsableLibera).append("' ");
//        }
//        if (liberado != null) {
//            this.bodyQuery.append("AND ug.LIBERADO='").append(liberado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("' ");
//        }
//        this.bodyQuery.append("AND g.ELIMINADO='False' ");
//        this.bodyQuery.append("AND ug.GERENCIA=g.ID ");
//        this.bodyQuery.append("AND ug.USUARIO=u.ID ");
//        this.bodyQuery.append("AND u.AP_CAMPO=c.ID ");
//        this.bodyQuery.append(" and nu.usuario = u.id ");
//
//        if (orderByField != null && !orderByField.isEmpty()) {
//            if ("id".equals(orderByField)) {
//                this.bodyQuery.append("ORDER BY ug.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
//            } else if ("nombre".equals(orderByField)) {
//                this.bodyQuery.append("ORDER BY u.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
//            }
//        }
//
//        Query query = em.createNativeQuery(this.bodyQuery.toString());
//
//        log(query.toString());
//
//        List<Object[]> result = query.getResultList();
//        List<UsuarioGerenciaVo> list = new ArrayList<UsuarioGerenciaVo>();
//        UsuarioGerenciaVo vo = null;
//
//        for (Object[] objects : result) {
//            vo = new UsuarioGerenciaVo();
//            vo.setId((Integer) objects[0]);
//            vo.setIdUsuario((String) objects[1]);
//            vo.setNombre((String) objects[2]);
//            vo.setIdApCampo((Integer) objects[3]);
//            vo.setNombreApCampo((String) objects[4]);
//            vo.setIdGerencia((Integer) objects[5]);
//            vo.setGerencia((String) objects[6]);
//            vo.setIdNoticia((Integer) objects[7]);
//            vo.setIdUsuarioLibero((String) objects[8]);
//            vo.setNombreUsuarioLibero((String) objects[9]);
//            vo.setIdRhCampoGerencia((Integer) objects[10]);
//
//            list.add(vo);
//        }
//
//        log("Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " RhUsuarioGerenciaImpl");
//
//        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
//    }
    /**
     * Valida si un Usuario ha sido liberado por todas las gerencias con las que
     * podría tener pendientes
     *
     * @param idUsuario
     * @return 'true' si no queda ninguna gerencia pendiente de liberar al
     * Usuario
     */
    public boolean validateUserIsReleased(String idUsuario) {
	List<UsuarioGerenciaVo> list = traerUsuarioNoLiberadoGerencia(idUsuario, Boolean.FALSE);
	return (list == null || list.isEmpty());
    }

    
    public void setFreeUsuario(int idUsuarioGerencia, String idUsuario) {

	log("idUsuarioGerencia: " + idUsuarioGerencia);

	RhUsuarioGerencia rhUsuarioGerencia = find(idUsuarioGerencia);
	rhUsuarioGerencia.setLiberado(Constantes.BOOLEAN_TRUE);
	rhUsuarioGerencia.setModifico(new Usuario(idUsuario));
	rhUsuarioGerencia.setFechaModifico(new Date());
	rhUsuarioGerencia.setHoraModifico(new Date());

	edit(rhUsuarioGerencia);

	log("RhUsuarioGerencia UPDATED SUCCESSFULLY");
    }

    
    public void setFreeUsuarioAndAdvicing(int idUsuarioGerencia, String idUsuario) throws EmailNotFoundException {
	RhUsuarioGerencia rhUsuarioGerencia = find(idUsuarioGerencia);
	setFreeUsuario(idUsuarioGerencia, idUsuario);

	if (validateUserIsReleased(rhUsuarioGerencia.getUsuario().getId())) {
	    this.notificacionUsuarioRemote.notificationUsuarioLiberadoCompletamente(idUsuarioGerencia);
	}
    }

    
    public RhUsuarioGerencia findByUsuarioAndGerencia(int idGerencia, String idUsuario) {

	RhUsuarioGerencia rhUsuarioGerencia = null;

	try {
	    rhUsuarioGerencia = (RhUsuarioGerencia) em.createQuery("SELECT e FROM RhUsuarioGerencia e WHERE e.eliminado = :eliminado AND e.usuario.id = :idUsuario AND e.gerencia.id = :idGerencia").setParameter("eliminado", (Constantes.NO_ELIMINADO)).setParameter("idGerencia", idGerencia).setParameter("idUsuario", idUsuario).getSingleResult();
	} catch (NonUniqueResultException nure) {
	    log(nure.getMessage());
	    log("Se encontró más de un resultado para el RhUsuarioGerencia para la Gerencia: " + idGerencia + " y Usuario: " + idUsuario);
	    return rhUsuarioGerencia;
	} catch (NoResultException nre) {
	    log(nre.getMessage());
	    log("No se encontró ningún RhUsuarioGerencia para la Gerencia: " + idGerencia + " y Usuario: " + idUsuario);
	    return rhUsuarioGerencia;
	}

	return rhUsuarioGerencia;
    }

    
    public boolean verficiarProcesoBaja(String idUsuario) {
	log("verficiarProcesoBaja" + idUsuario);
	try {
	    clearBodyQuery();
	    bodyQuery.append(" select * ");
	    bodyQuery.append(" from RH_USUARIO_GERENCIA u");
	    bodyQuery.append(" where u.USUARIO = '").append(idUsuario).append("'");
	    bodyQuery.append(" and u.ELIMINADO = 'False' ");

	    if (em.createNativeQuery(bodyQuery.toString()).getSingleResult() != null) {
		log("El usuario esta en proceso de baja");
		return true;
	    }
	    return false;
	} catch (NonUniqueResultException nure) {
	    log(nure.getMessage());
	    log("El usuario esta en proces de baja" + nure.getMessage());
	    return true;
	} catch (NoResultException nre) {
	    log(nre.getMessage());
	    log("El usuario NO esta en proces de baja :" + nre.getMessage());
	    return false;
	}

    }

    
    public List<UsuarioGerenciaVo> traerUsuarioNoLiberadoGerencia(String idUsuario, Boolean liberado) {
	log("SgViajeImpl.traerUsuarioNoLiberadoGerencia()");
	List<UsuarioGerenciaVo> lv = null;
	List<Object[]> l;
	List<UsuarioGerenciaVo> ltemp = null;
	UsuarioGerenciaVo o;
	String porUsuario = "";
	if (!idUsuario.isEmpty()) {
	    porUsuario = " and ug.USUARIO = '" + idUsuario + "'";
	}
	try {
	    clearBodyQuery();
	    bodyQuery.append("SELECT ug.id, u.id, u.nombre, g.id, g.nombre, u.fecha_baja, ug.liberado, u.activo, u.eliminado, cg.id");
	    bodyQuery.append(" FROM  rh_usuario_gerencia ug, usuario u, rh_campo_gerencia cg, gerencia g");
	    bodyQuery.append(" WHERE ug.usuario = u.id ");// and ug.liberado = '".concat(Constantes.BOOLEAN_FALSE));
	    if (liberado != null) {
		this.bodyQuery.append("AND ug.LIBERADO='").append(liberado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("' ");
	    }
	    bodyQuery.append(" and ug.eliminado = false ");
	    bodyQuery.append(" and ug.rh_campo_gerencia = cg.id");
	    bodyQuery.append(" and cg.gerencia = g.id");
	    bodyQuery.append(" and u.eliminado =  false ");
	    bodyQuery.append(porUsuario);
	    bodyQuery.append(" order by g.nombre asc");

	    l = em.createNativeQuery(bodyQuery.toString()).getResultList();
	    if (l != null && !l.isEmpty()) {
		lv = new ArrayList<UsuarioGerenciaVo>();
		for (Object[] objects : l) {
		    lv.add(castUsuarioGerenciaVO(objects));
		}

		// nueva lista
		TreeSet<String> nombre = new TreeSet<String>();
		for (UsuarioGerenciaVo usuarioGerenciaVo : lv) {
		    nombre.add(usuarioGerenciaVo.getNombre());
		}
		//
		int i = 0;
		ltemp = new ArrayList<UsuarioGerenciaVo>();
		for (String string : nombre) {
		    List<UsuarioGerenciaVo> lt = new ArrayList<UsuarioGerenciaVo>();
		    o = new UsuarioGerenciaVo();
		    o.setNombre(string);
		    for (UsuarioGerenciaVo usuarioGerenciaVo : lv) {
			if (string.equals(usuarioGerenciaVo.getNombre())) {
			    lt.add(new UsuarioGerenciaVo(usuarioGerenciaVo.getGerencia(), usuarioGerenciaVo.getLiberado()));
			    if (usuarioGerenciaVo.getLiberado().equals(Constantes.BOOLEAN_FALSE)) {
				i++;
			    }
			    //llena y sobre escribe lo datos del usuario
			    o.setFechaBaja(usuarioGerenciaVo.getFechaBaja());
			    o.setIdUsuario(usuarioGerenciaVo.getIdUsuario());
			    o.setActivo(usuarioGerenciaVo.getActivo());
			    o.setEliminado(usuarioGerenciaVo.isEliminado());
			}
		    } // fin del for interno
		    if (i == 0) {
			o.setTerminarBaja(true);
		    } else {
			o.setTerminarBaja(false);
		    }
		    o.setGerenciaUsuario(lt);//
		    ltemp.add(o);
		    i = 0;
		    log("Lista tempo: " + ltemp.size());
		}
	    }
	} catch (Exception e) {
	    e.getStackTrace();
	    log("Error al traer usuario  gerenciad " + e.getMessage());
	    e.printStackTrace();
	    return null;
	}
	return ltemp;
    }

    private UsuarioGerenciaVo castUsuarioGerenciaVO(Object[] objects) {
	UsuarioGerenciaVo vo;
	try {
	    vo = new UsuarioGerenciaVo();
	    vo.setIdUsuarioGerencia((Integer) objects[0]);
	    vo.setIdUsuario((String) objects[1]);
	    vo.setNombre((String) objects[2]);
	    vo.setIdGerencia((Integer) objects[3]);
	    vo.setGerencia((String) objects[4]);
	    vo.setFechaBaja((Date) objects[5]);
	    vo.setLiberado((String) objects[6]);
	    vo.setActivo((String) objects[7]);
	    vo.setEliminado((Boolean) objects[8]);
	    vo.setIdRhCampoGerencia((Integer) objects[9]);
	    return vo;
	} catch (Exception e) {
	    e.getStackTrace();
	    return null;
	}
    }

    
    public List<UsuarioGerenciaVo> findAllForFreeByUsuario(String idUsuario) {
	clearBodyQuery();
	this.bodyQuery.append("SELECT ug.ID, "); //0
	this.bodyQuery.append("u.ID AS ID_USUARIO, "); //1
	this.bodyQuery.append("u.NOMBRE AS NOMBRE_USUARIO, "); //2
	this.bodyQuery.append("c.ID AS ID_AP_CAMPO, "); //3
	this.bodyQuery.append("c.NOMBRE AS NOMBRE_AP_CAMPO, "); //4
	this.bodyQuery.append("nu.CO_NOTICIA AS ID_CO_NOTICIA, "); //5
	this.bodyQuery.append("ug.RH_CAMPO_GERENCIA AS ID_RH_CAMPO_GERENCIA, "); //6
	this.bodyQuery.append("(SELECT g.NOMBRE FROM GERENCIA g WHERE g.id=(SELECT cg.GERENCIA FROM RH_CAMPO_GERENCIA cg WHERE cg.ID=ug.RH_CAMPO_GERENCIA)) AS NOMBRE_GERENCIA "); //7
	this.bodyQuery.append("FROM RH_USUARIO_GERENCIA ug, USUARIO u, AP_CAMPO c, CO_NOTICIA_USUARIO nu, CO_NOTICIA n  ");
	this.bodyQuery.append("WHERE ug.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
	this.bodyQuery.append("AND ug.LIBERADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
	this.bodyQuery.append("AND u.ELIMINADO ='").append(Constantes.BOOLEAN_FALSE).append("' ");
	this.bodyQuery.append("AND ug.USUARIO=u.ID ");
	this.bodyQuery.append("AND u.AP_CAMPO=c.ID ");
	this.bodyQuery.append("AND nu.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
	this.bodyQuery.append("AND nu.USUARIO=u.ID ");
	this.bodyQuery.append("AND nu.SG_TIPO=23 ");
	this.bodyQuery.append("AND nu.CO_NOTICIA=n.ID ");
	this.bodyQuery.append("AND ug.RH_CAMPO_GERENCIA IN (SELECT tg.RH_CAMPO_GERENCIA FROM RH_TIPO_GERENCIA tg WHERE tg.USUARIO='").append(idUsuario).append("' AND tg.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("') ");
	this.bodyQuery.append("ORDER BY u.NOMBRE DESC");
	//
	Query query = em.createNativeQuery(this.bodyQuery.toString());

	log(query.toString());

	List<Object[]> result = query.getResultList();
	List<UsuarioGerenciaVo> list = new ArrayList<UsuarioGerenciaVo>();
	UsuarioGerenciaVo vo = null;

	for (Object[] objects : result) {
	    vo = new UsuarioGerenciaVo();
	    vo.setId((Integer) objects[0]);
	    vo.setIdUsuario((String) objects[1]);
	    vo.setNombre((String) objects[2]);
	    vo.setIdApCampo((Integer) objects[3]);
	    vo.setNombreApCampo((String) objects[4]);
//            vo.setIdGerencia((Integer) objects[5]);
//            vo.setGerencia((String) objects[6]);
	    vo.setIdNoticia((Integer) objects[5]);
//            vo.setIdUsuarioLibero((String) objects[6]);
//            vo.setNombreUsuarioLibero((String) objects[7]);
	    vo.setIdRhCampoGerencia((Integer) objects[6]);
	    vo.setGerencia((String) objects[7]);

	    list.add(vo);
	}

	log("Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " RhUsuarioGerencia (Usuarios for free)");

	return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    private void log(String mensaje) {
	UtilLog4j.log.info(this, mensaje);
    }

    
    public long totalUsuario(String isUsuario) {
	clearQuery();
	query.append("select count(*) FROM RH_USUARIO_GERENCIA ug  WHERE ug.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("'");
	query.append("	 AND ug.LIBERADO='").append(Constantes.BOOLEAN_FALSE).append("'");
	query.append("	AND ug.RH_CAMPO_GERENCIA ");
	query.append("	    IN (SELECT tg.RH_CAMPO_GERENCIA FROM RH_TIPO_GERENCIA tg WHERE tg.USUARIO='").append(isUsuario).append("'");
	query.append(" AND tg.ELIMINADO='False') ");
	//
	return ((Long) em.createNativeQuery(query.toString()).getSingleResult());
    }

    
    public long totalUsuarioPorFinalizarBaja() {
	clearQuery();
	query.append("select count(distinct(ug.USUARIO)) FROM RH_USUARIO_GERENCIA ug ");
	query.append("	 inner join USUARIO u on ug.USUARIO = u.ID ");
	query.append("	WHERE ug.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("	and u.ELIMINADO =  '").append(Constantes.NO_ELIMINADO).append("'");
	//
	return ((Long) em.createNativeQuery(query.toString()).getSingleResult());
    }
}
