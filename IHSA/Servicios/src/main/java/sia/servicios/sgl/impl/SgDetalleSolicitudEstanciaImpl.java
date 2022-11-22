/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import com.newrelic.api.agent.Trace;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgDetalleSolicitudEstancia;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgViajero;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.vo.DetalleSolicitudVO;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgDetalleSolicitudEstanciaImpl extends AbstractFacade<SgDetalleSolicitudEstancia>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgDetalleSolicitudEstanciaImpl() {
	super(SgDetalleSolicitudEstancia.class);
    }
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private EstatusImpl estatusRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    private SgInvitadoImpl sgInvitadoRemote;
    @Inject
    private SiMovimientoImpl siMovimientoService;
    @Inject
    private SiOperacionImpl siOperacionService;
    @Inject
    private SgTipoImpl sgTipoRemote;
    @Inject
    private SgSolicitudEstanciaSiMovimientoImpl sgSolicitudEstSiMovimientoService;
    @Inject
    private SgDetalleSolicitudEstanciaSiMovimientoImpl sgDetalleSolicitudEstanciaSiMovimientoService;

    
    public List<DetalleEstanciaVO> traerDetallePorSolicitud(int idSolicitudEstancia, boolean eliminado) {
	List<DetalleEstanciaVO> integrantes = null;
	clearQuery();
	try {
	    query.append(consulta());
	    query.append(" WHERE de.SG_SOLICITUD_ESTANCIA = ").append(idSolicitudEstancia);
	    query.append(" and de.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" and de.ELIMINADO = '").append(eliminado).append("'");
	    query.append(" order by de.ID asc");

	    UtilLog4j.log.info(this, "Q: detalle soliciutud : " + query.toString());
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		integrantes = new ArrayList<DetalleEstanciaVO>();
		for (Object[] objects : lo) {
		    integrantes.add(castDetalleEstancia(objects));
		}
		return integrantes;
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    return null;
	}
    }

    private String consulta() {
	String q = " SELECT de.id,g.id,se.id, te.id, se.CODIGO, se.DIAS_ESTANCIA, g.NOMBRE, te.NOMBRE, u.id, u.NOMBRE, "
		+ " inv.id, inv.NOMBRE, de.DESCRIPCION, se.INICIO_ESTANCIA, se.FIN_ESTANCIA, de.REGISTRADO, de.CANCELADO,  "
		+ " u.email "
		+ " FROM SG_DETALLE_SOLICITUD_ESTANCIA de "
		+ "	    inner join SG_SOLICITUD_ESTANCIA se on de.SG_SOLICITUD_ESTANCIA  = se.id"
		+ "	    inner join GERENCIA g on se.gerencia = g.id"
		+ "	    inner join SG_TIPO_ESPECIFICO te on de.SG_TIPO_ESPECIFICO = te.id"
		+ "	    left join usuario u on de.usuario = u.id"
		+ "	    left join sg_invitado inv on de.sg_invitado = inv.id";
	return q;
    }

    
    public List<DetalleEstanciaVO> getAllIntegrantesBySolicitud(int idSolicitudEstancia, Boolean registrado, Boolean cancelado, Boolean eliminado) {
	List<DetalleEstanciaVO> integrantes = new ArrayList<DetalleEstanciaVO>();
	clearQuery();
	try {
	    query.append(consulta());
	    query.append(" WHERE de.SG_SOLICITUD_ESTANCIA = ").append(idSolicitudEstancia);
	    if (registrado != null) {
		query.append(" and de.REGISTRADO = '").append(registrado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
	    }
	    if (cancelado != null) {
		query.append(" and de.CANCELADO = '").append(cancelado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
	    }
	    if (eliminado != null) {
		query.append(" and de.ELIMINADO = '").append(eliminado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
	    }
	    query.append(" order by de.ID asc");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		for (Object[] objects : lo) {
		    integrantes.add(castDetalleEstancia(objects));
		}
		return integrantes;
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "  + + + Error al recuperar el detalle de estancias: " + e.getMessage());
	    e.getStackTrace();
	    return null;
	}
    }

    private DetalleEstanciaVO castDetalleEstancia(Object[] objects) {
	DetalleEstanciaVO devo = new DetalleEstanciaVO();
	devo.setIdDetalleEstancia((Integer) objects[0]);
	devo.setIdGerencia((Integer) objects[1]);
	devo.setIdSolicitudEstancia((Integer) objects[2]);
	devo.setIdTipoEspecifico((Integer) objects[3]);
	devo.setCodigo((String) objects[4]);
	devo.setDias((Integer) objects[5]);
	devo.setGerencia((String) objects[6]);
	devo.setTipoDetalle((String) objects[7]);
	devo.setIdUsuario(objects[8] != null ? (String) objects[8] : "");
	devo.setUsuario((String) objects[9]);
	devo.setIdInvitado(objects[10] != null ? (Integer) objects[10] : 0);
	devo.setInvitado((String) objects[11]);
	devo.setDescripcion((String) objects[12]);
	devo.setInicioEstancia((Date) objects[13]);
	devo.setFinEstancia((Date) objects[14]);
	devo.setRegistrado((Boolean) objects[15]);
	devo.setCancelado((Boolean) objects[16]);
	devo.setCorreoUsuario((String) objects[17]);
	return devo;
    }

    
    public void guardarDetalleSolicitud(Usuario usuario, SgSolicitudEstanciaVo sgSolicitudEstancia, SgTipo sgTipo, List<DetalleSolicitudVO> lu, boolean eliminado) {
	for (DetalleSolicitudVO detalleSolicitudVO : lu) {
	    SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = new SgDetalleSolicitudEstancia();

	    if (detalleSolicitudVO.getTipoEspecifico() == Constantes.EMPLEADO) {
		UtilLog4j.log.info(this, "Usuario: " + detalleSolicitudVO.getIdInvitado());
		SgTipoEspecifico tipoEspecificoUsuario = sgTipoEspecificoRemote.find(Constantes.EMPLEADO); //es empleado

		sgDetalleSolicitudEstancia.setUsuario(usuarioRemote.buscarPorNombre(detalleSolicitudVO.getNombre()));
		sgDetalleSolicitudEstancia.setSgInvitado(null);
		sgDetalleSolicitudEstancia.setSgTipoEspecifico(tipoEspecificoUsuario);

		//Actualizar el campo 'usado' a True del tipoEspecifico
		tipoEspecificoUsuario.setFechaGenero(new Date());
		tipoEspecificoUsuario.setHoraGenero(new Date());
		tipoEspecificoUsuario.setUsado(Constantes.BOOLEAN_TRUE);
		sgTipoEspecificoRemote.edit(tipoEspecificoUsuario);
	    } else {
		UtilLog4j.log.info(this, "Invitado: " + detalleSolicitudVO.getIdInvitado());
		SgTipoEspecifico tipoEspecificoInvitado = sgTipoEspecificoRemote.find(Constantes.INVITADO);
		sgDetalleSolicitudEstancia.setUsuario(null);
		sgDetalleSolicitudEstancia.setSgInvitado(sgInvitadoRemote.find(detalleSolicitudVO.getIdInvitado()));
		sgDetalleSolicitudEstancia.setSgTipoEspecifico(tipoEspecificoInvitado);

		//Actualizar el campo 'usado' a True del tipoEspecifico
		tipoEspecificoInvitado.setFechaGenero(new Date());
		tipoEspecificoInvitado.setHoraGenero(new Date());
		tipoEspecificoInvitado.setUsado(Constantes.BOOLEAN_TRUE);
		sgTipoEspecificoRemote.edit(tipoEspecificoInvitado);
	    }

	    sgDetalleSolicitudEstancia.setSgTipo(sgTipo);
	    sgDetalleSolicitudEstancia.setSgSolicitudEstancia(sgSolicitudEstanciaRemote.find(sgSolicitudEstancia.getId()));
	    sgDetalleSolicitudEstancia.setGenero(usuario);
	    sgDetalleSolicitudEstancia.setFechaGenero(new Date());
	    sgDetalleSolicitudEstancia.setHoraGenero(new Date());
	    sgDetalleSolicitudEstancia.setDescripcion(detalleSolicitudVO.getDescripcion());
	    sgDetalleSolicitudEstancia.setEliminado(eliminado);
	    sgDetalleSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
	    sgDetalleSolicitudEstancia.setRegistrado(Constantes.BOOLEAN_FALSE);
	    create(sgDetalleSolicitudEstancia);
	}
    }

    
    @Trace
    public void guardarHuespededSolicitudEstancia(String idSesion, int iddsolicitudEstancia, boolean empleado, String idHuesped, int idInvitado, String descripcion) {
	SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = new SgDetalleSolicitudEstancia();

	if (empleado) {
	    UtilLog4j.log.info(this, "Usuario: " + idHuesped);
	    sgDetalleSolicitudEstancia.setUsuario(new Usuario(idHuesped));
	    sgDetalleSolicitudEstancia.setSgInvitado(null);
	    sgDetalleSolicitudEstancia.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.EMPLEADO));

	} else {
	    UtilLog4j.log.info(this, "Invitado: " + idInvitado);
	    sgDetalleSolicitudEstancia.setUsuario(null);
	    sgDetalleSolicitudEstancia.setSgInvitado(sgInvitadoRemote.find(idInvitado));
	    sgDetalleSolicitudEstancia.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.INVITADO));
	}

	sgDetalleSolicitudEstancia.setSgTipo(sgTipoRemote.find(Constantes.TIPO_GENERAL_DETALLE_SOLICITUD));
	sgDetalleSolicitudEstancia.setSgSolicitudEstancia(sgSolicitudEstanciaRemote.find(iddsolicitudEstancia));
	sgDetalleSolicitudEstancia.setGenero(new Usuario(idSesion));
	sgDetalleSolicitudEstancia.setFechaGenero(new Date());
	sgDetalleSolicitudEstancia.setHoraGenero(new Date());
	sgDetalleSolicitudEstancia.setDescripcion(descripcion);
	sgDetalleSolicitudEstancia.setEliminado(Constantes.NO_ELIMINADO);
	sgDetalleSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
	sgDetalleSolicitudEstancia.setRegistrado(Constantes.BOOLEAN_FALSE);
	sgDetalleSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
	create(sgDetalleSolicitudEstancia);
    }

    
    public void modificarDetalleSolicitud(Usuario usuario, DetalleEstanciaVO detalleSolicitudEstancia, boolean eliminado, String user, int idInvitado) {
	try {
	    SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = find(detalleSolicitudEstancia.getIdDetalleEstancia());
	    if (idInvitado > 0) {

		sgDetalleSolicitudEstancia.setSgInvitado(sgInvitadoRemote.find(idInvitado));
		sgDetalleSolicitudEstancia.setUsuario(null);
	    } else {
		Usuario u = usuarioRemote.buscarPorNombre(user);
//////                if (u == null) {
//////                    sgDetalleSolicitudEstancia.setUsuario(null);
//////                    sgDetalleSolicitudEstancia.setSgTipoEspecifico(sgTipoEspecificoRemote.find(20));
//////                } else {
		sgDetalleSolicitudEstancia.setUsuario(u);
		sgDetalleSolicitudEstancia.setSgInvitado(null);
		sgDetalleSolicitudEstancia.setSgTipoEspecifico(sgTipoEspecificoRemote.find(19));
//////                }
	    }
	    sgDetalleSolicitudEstancia.setGenero(usuario);
	    sgDetalleSolicitudEstancia.setFechaGenero(new Date());
	    sgDetalleSolicitudEstancia.setHoraGenero(new Date());
	    sgDetalleSolicitudEstancia.setEliminado(eliminado);
	    edit(sgDetalleSolicitudEstancia);

	} catch (Exception e) {
	}
    }

    
    public void eliminarDetalleSolicitud(Usuario usuario, DetalleEstanciaVO detalleSolicitudEstancia, boolean eliminado) {
	try {
	    SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = find(detalleSolicitudEstancia.getIdDetalleEstancia());
	    sgDetalleSolicitudEstancia.setGenero(usuario);
	    sgDetalleSolicitudEstancia.setFechaGenero(new Date());
	    sgDetalleSolicitudEstancia.setHoraGenero(new Date());
	    sgDetalleSolicitudEstancia.setEliminado(eliminado);
	    edit(sgDetalleSolicitudEstancia);

	} catch (Exception e) {
	}
    }

    
    public void cancelarSolicitudRegistroHuesped(Usuario usuario, int detalleSolicitudEstancia, boolean notificar) {
	boolean v;
	try {
	    //Envio de mail desde una a otra
	    SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = find(detalleSolicitudEstancia);
	    //int idInvitado, String invitado, String empleado, String gerencia, Date inicio, Date fin, String codigo, String nombreGenero, String tipoDetalle
	    v = notificacionServiciosGeneralesRemote.enviaCorreoCancelacionAntesDeRegistroHuesped(usuario, sgDetalleSolicitudEstancia.getSgInvitado() != null ? sgDetalleSolicitudEstancia.getSgInvitado().getId() : 0,
		    sgDetalleSolicitudEstancia.getSgInvitado() != null ? sgDetalleSolicitudEstancia.getSgInvitado().getNombre() : "",
		    sgDetalleSolicitudEstancia.getUsuario() != null ? sgDetalleSolicitudEstancia.getUsuario().getNombre() : "",
		    sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getGerencia().getNombre(),
		    sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getInicioEstancia(), sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getFinEstancia(),
		    sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getCodigo(), sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getGenero().getNombre(),
		    sgDetalleSolicitudEstancia.getSgTipoEspecifico().getNombre(), sgDetalleSolicitudEstancia.getGenero().getEmail(), sgDetalleSolicitudEstancia.getSgSolicitudEstancia().getGerencia().getId());
	    if (v) {
		UtilLog4j.log.info(this, "cancelar el regitro de detalle ");
		//SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = find(detalleSolicitudEstancia.getId());
		//Modificacion a a la solicitud de estancia
		sgDetalleSolicitudEstancia.setGenero(usuario);
		sgDetalleSolicitudEstancia.setFechaGenero(new Date());
		sgDetalleSolicitudEstancia.setHoraGenero(new Date());
		sgDetalleSolicitudEstancia.setCancelado(Constantes.BOOLEAN_TRUE);
		edit(sgDetalleSolicitudEstancia);
		/*
		 * Este metodo se encarga de verificar si una solicitud esta
		 * lista para cancelarce por el motivo de que el detalle o
		 * integrante de la misma sea uno solo y este se cancele, por
		 * default debe de cancelarce. cambio el codigo a un metodo para
		 * utilizarlo de mas metodos
		 */

		//   vefiricarCancelacionSolicitudEstancia(sgDetalleSolicitudEstancia.getSgSolicitudEstancia(), "", usuario, notificar);
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al cancelar la solicitud de huesped...");
	}
    }

    private void vefiricarCancelacionSolicitudEstancia(SgSolicitudEstancia solicitudEstancia, String motivoCancelacion, Usuario usuario, boolean notificar) throws SIAException, Exception {
	UtilLog4j.log.info(this, "verificacr solicitud");
	//Para cambiar el estado de la solicitud de estancia
	int c = 0;
	int d = 0;
	//SgSolicitudEstancia sgSolicitudEstancia = sgSolicitudEstanciaRemote.find(solicitudEstanciaVo.getId());
	List<DetalleEstanciaVO> lista = traerDetallePorSolicitud(solicitudEstancia.getId(), Constantes.NO_ELIMINADO);
	for (DetalleEstanciaVO sgDetSol : lista) {
	    if (!sgDetSol.isCancelado()) {
		c++;
	    }
	}
	for (DetalleEstanciaVO detalleEstanciaVO : lista) {
	    if (!detalleEstanciaVO.isCancelado() && detalleEstanciaVO.isRegistrado()) {
		d++;
	    }
	}
	if (c == 0) {
	    //Marcar como Cancelados a los Integrantes de la Solicitud de Estancia
	    //Se registra el cambio
	    UtilLog4j.log.info(this, "Estancia id: " + solicitudEstancia.getId());
	    solicitudEstancia.setModifico(usuario);
	    solicitudEstancia.setFechaModifico(new Date());
	    solicitudEstancia.setHoraModifico(new Date());
	    solicitudEstancia.setCancelado(Constantes.BOOLEAN_TRUE);
	    solicitudEstancia.setEstatus(estatusRemote.find(50));
	    sgSolicitudEstanciaRemote.edit(solicitudEstancia);
	    //enviar a si movimiento
	    SiMovimiento simo = this.siMovimientoService.guardarSiMovimiento("Se canceló desde el viajero", siOperacionService.find(3), usuario);
	    if (simo != null) {
		this.sgSolicitudEstSiMovimientoService.guardarSiMovimiento(solicitudEstancia.getId(), simo.getId(), usuario.getId());
	    }
	    UtilLog4j.log.info(this, "Despues de guardar el id " + solicitudEstancia.getId());
	}

	if (d == lista.size()) {
	    sgSolicitudEstanciaRemote.finalizaSolicitud(solicitudEstancia.getId(), usuario.getId());
	}
    }

    
    public boolean cancelLoungeViajeroOfRequest(SgViajero viajero, String motivoCancelacion, Usuario usuario) {
	boolean ret = false;
	try {
	    SgDetalleSolicitudEstancia detalle = findDetalleSolicitudEstancia(viajero);
	    if (detalle != null) {
		UtilLog4j.log.info(this, "detalle ok");
		cancelarSolicitudRegistroHuesped(usuario, detalle.getId(), false);
		// el Movimiento va aqui por que en el metodo de cancelar solicitud no existe un motivo de cancelación
		SiMovimiento simo = this.siMovimientoService.guardarSiMovimiento(motivoCancelacion, this.siOperacionService.find(3), usuario);
		UtilLog4j.log.info(this, "se envio a somovimiento ");
		SiMovimiento si = siMovimientoService.find(simo.getId());
		//ENVIAR A RELACION
		this.sgDetalleSolicitudEstanciaSiMovimientoService.guardarDetalleSolicitudEstanciaSiMovimiento(detalle, si.getId(), usuario);

		ret = true;
	    }
	    UtilLog4j.log.info(this, "termino cancelar viajero");
	    return ret;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion al cancelar la estancia del viaero" + e.getMessage());
	    return ret;
	}

    }

    private SgDetalleSolicitudEstancia findDetalleSolicitudEstancia(SgViajero viajero) {
	try {
	    String comodin;

	    comodin = viajero.getUsuario() != null ? " AND d.usuario.id = '" + viajero.getUsuario().getId() + "'" : " AND d.sgInvitado.id = " + viajero.getSgInvitado().getId();
	    UtilLog4j.log.info(this, "comodin  " + comodin);
	    return (SgDetalleSolicitudEstancia) em.createQuery("SELECT d FROM SgDetalleSolicitudEstancia d"
		    + " WHERE d.sgSolicitudEstancia.id = :idSol "
		    + comodin
		    + " AND d.eliminado = :eli").setParameter("idSol", viajero.getSgSolicitudEstancia().getId()).setParameter("eli", Constantes.BOOLEAN_FALSE).getSingleResult();

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Expepcion al buscar el detalle de la solicitud " + e.getMessage());
	    return null;
	}
    }

    
    public List<SgDetalleSolicitudEstancia> getDetailByRequestAndUser(int idSolicitudEstancia, String idUser, int idInvitado) {
	Query e;
	String q = "SELECT d FROM SgDetalleSolicitudEstancia d WHERE d.sgSolicitudEstancia.id = :idSol AND d.eliminado = :eli AND ";
	if (idInvitado == 0) {
	    q += "d.usuario.id  = :user ";
	    e = em.createQuery(q);
	    e.setParameter("user", idUser);
	} else {
	    q += "d.sgInvitado.id  = :inv ";
	    e = em.createQuery(q);
	    e.setParameter("inv", idInvitado);
	}
	e.setParameter("idSol", idSolicitudEstancia).setParameter("eli", Constantes.NO_ELIMINADO);
	UtilLog4j.log.info(this, "Query seth: " + q);
	UtilLog4j.log.info(this, "Query seth: " + e.toString());
	return e.getResultList();

    }
}
