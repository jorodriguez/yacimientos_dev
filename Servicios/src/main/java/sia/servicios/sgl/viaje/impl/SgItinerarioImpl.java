/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

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
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgItinerario;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.HistorialItinerarioVO;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgItinerarioImpl extends AbstractFacade<SgItinerario> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private SiCiudadImpl siCiudadRemote;
    @Inject
    private SgDetalleItinerarioImpl sgDetalleItinerarioRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    //
    @Inject
    private NotificacionViajeImpl notificacionViajeRemote;    
    private StringBuilder sb = new StringBuilder();

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    private void limpiar() {
	sb.delete(0, sb.length());
    }

    public SgItinerarioImpl() {
	super(SgItinerario.class);
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void save(SgItinerario sgItinerario, String idUsuario) {
	UtilLog4j.log.info(this, "SgItinerarioImpl.save()");

	sgItinerario.setGenero(new Usuario(idUsuario));
	sgItinerario.setFechaGenero(new Date());
	sgItinerario.setHoraGenero(new Date());
	sgItinerario.setEliminado(Constantes.NO_ELIMINADO);
	sgItinerario.setNotificado(Constantes.BOOLEAN_FALSE);

	super.create(sgItinerario);
	UtilLog4j.log.info(this, "SgItinerario CREATED SUCCESSFULLY");
    }

    
    public void save(int idSgSolicitudViaje, int idSiCiudadOrigen, int idSiCiudadDestino, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgItinerarioImpl.save()");

	SgItinerario sgItinerario = new SgItinerario();
	sgItinerario.setSgSolicitudViaje(this.sgSolicitudViajeRemote.find(idSgSolicitudViaje));
	sgItinerario.setSiCiudadOrigen(this.siCiudadRemote.find(idSiCiudadOrigen));
	sgItinerario.setSiCiudadDestino(this.siCiudadRemote.find(idSiCiudadDestino));
	sgItinerario.setGenero(new Usuario(idUsuario));
	sgItinerario.setFechaGenero(new Date());
	sgItinerario.setHoraGenero(new Date());
	sgItinerario.setEliminado(Constantes.NO_ELIMINADO);
	sgItinerario.setNotificado(Constantes.BOOLEAN_FALSE);

	super.create(sgItinerario);
	UtilLog4j.log.info(this, "SgItinerario CREATED SUCCESSFULLY");
    }

    
    //@TransactionAttribute(TransactionAttributeType.REQUIRED)
    //public SgItinerario update(SgItinerario itinerario, String idUsuario) {
    public SgItinerario update(int idItinerario, int idCiudadOrigen, int idCiudadDestino, String idUsuario) {
	UtilLog4j.log.info(this, "SgItinerarioImpl.update()");
	SgItinerario itinerario = find(idItinerario);
	if (itinerario != null) {
	    itinerario.setSiCiudadOrigen(siCiudadRemote.find(idCiudadOrigen));
	    itinerario.setSiCiudadDestino(siCiudadRemote.find(idCiudadDestino));
	    itinerario.setModifico(new Usuario(idUsuario));
	    itinerario.setFechaModifico(new Date());
	    itinerario.setHoraModifico(new Date());

	    edit(itinerario);
	    UtilLog4j.log.info(this, "SgItinerario UPDATED SUCCESSFULLY");
	}

	return itinerario;
    }

    
    public void delete(int idItinerario, String idUsuario) {
	UtilLog4j.log.info(this, "SgItinerarioImpl.delete()");
	SgItinerario itinerario = find(idItinerario);
	if (itinerario != null) {
	    itinerario.setModifico(new Usuario(idUsuario));
	    itinerario.setFechaModifico(new Date());
	    itinerario.setHoraModifico(new Date());
	    itinerario.setEliminado(Constantes.ELIMINADO);

	    edit(itinerario);
	    UtilLog4j.log.info(this, "SgItinerario DELETED SUCCESSFULLY");
	}
    }

    
    public ItinerarioCompletoVo buscarItinerarioCompletoVoPorIdSolicitud(int idSolicitudViaje, boolean esIda, boolean conEscalas, String campoOrdenar) {
	UtilLog4j.log.info(this, "### buscarItinerarioCompletoVo ### Solicitud " + idSolicitudViaje + " IDA " + esIda);
	Query q;
	try {
	    limpiar();
	    sb.append(" Select it.ID, ");//0
	    sb.append(" ciudadOrigen.id as id_ciudad_origen,");//1
	    sb.append(" ciudadOrigen.NOMBRE,");//2
	    sb.append(" ciudadDestino.id as id_ciudad_destino,");//3
	    sb.append(" ciudadDestino.NOMBRE,       ");//4
	    sb.append(" it.notificado,      ");//5
	    sb.append(" it.genero   ");//6
	    sb.append(" From sg_itinerario it, ");
	    sb.append(" SI_CIUDAD ciudadOrigen,");
	    sb.append(" SI_CIUDAD ciudadDestino");
	    sb.append(" Where it.SG_SOLICITUD_VIAJE = ").append(idSolicitudViaje);
	    sb.append(" and it.SI_CIUDAD_ORIGEN = ciudadOrigen.id");
	    sb.append(" and it.SI_CIUDAD_DESTINO = ciudadDestino.id");
	    sb.append(" and it.IDA = '").append(esIda ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
	    sb.append(" ORDER BY ").append(campoOrdenar);

	    Object[] lo = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
	    UtilLog4j.log.info(this, "))))))))))))))ITINERARIO " + lo);
	    return castItinerarioCompletoVo(lo, conEscalas);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "excepcion en traer el detalle de itinerario " + e.getMessage());
	    return null;
	}
    }

    
    public ItinerarioCompletoVo buscarItinerarioCompletoVoPorIdItinerario(int idItinerario, boolean conEscalas, String campoOrdenar) {
	UtilLog4j.log.info(this, "### buscarItinerarioCompletoVo ### id itinerario " + idItinerario + " con escalas  " + conEscalas);
	Query q;
	try {
	    limpiar();
	    sb.append(" Select it.ID, ");//0
	    sb.append(" ciudadOrigen.id as id_ciudad_origen,");//1
	    sb.append(" ciudadOrigen.NOMBRE,");//2
	    sb.append(" ciudadDestino.id as id_ciudad_destino,");//3
	    sb.append(" ciudadDestino.NOMBRE,");//4
	    sb.append(" it.notificado, ");//5
	    sb.append(" it.genero   ");//6
	    sb.append(" From sg_itinerario it, ");
	    sb.append(" SI_CIUDAD ciudadOrigen,");
	    sb.append(" SI_CIUDAD ciudadDestino");
	    sb.append(" Where it.id = ").append(idItinerario);
	    sb.append(" and it.SI_CIUDAD_ORIGEN = ciudadOrigen.id");
	    sb.append(" and it.SI_CIUDAD_DESTINO = ciudadDestino.id");
	    //sb.append(" and it.IDA = '").append(esIda ? Constantes.BOOLEAN_TRUE: Constantes.BOOLEAN_FALSE);
	    UtilLog4j.log.info(this, sb.toString());
	    Object[] lo = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
	    return castItinerarioCompletoVo(lo, conEscalas);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "excepcion en traer el detalle de itinerario " + e.getMessage());
	    return null;
	}
    }

    private ItinerarioCompletoVo castItinerarioCompletoVo(Object[] obj, boolean conEscalas) {
	UtilLog4j.log.info(this, "Comenzando con el casteo");
	ItinerarioCompletoVo vo = new ItinerarioCompletoVo();
	vo.setId((Integer) obj[0]);
	vo.setIdCiudadOrigen((Integer) obj[1]);
	vo.setNombreCiudadOrigen((String) obj[2]);
	vo.setIdCiudadDestino((Integer) obj[3]);
	vo.setNombreCiudadDestino((String) obj[4]);
	vo.setNotificado(((Boolean) obj[5]));

	if (conEscalas) {
	    UtilLog4j.log.info(this, "llenar las escalas " + vo.getId());
	    vo.setEscalas(sgDetalleItinerarioRemote.traerDetalleItinerario(vo.getId()));
	}
	vo.setGenero((String) obj[6]);
	return vo;
    }

    
    public List<SgItinerario> findAll(String orderByField, String orderByOrder, boolean eliminado) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgItinerarioImpl.findAll()");
	List<SgItinerario> itinearios = Collections.EMPTY_LIST;
	String query = "SELECT a FROM SgItinerario a WHERE a.eliminado = :eliminado";
	if (orderByField != null && !orderByField.equals("") && orderByOrder != null && !orderByOrder.equals("")) {
	    query += " ORDER BY a." + orderByField + " " + orderByOrder;
	}
	Query q = em.createQuery(query);
	//Asignando parámetros
	q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
	itinearios = q.getResultList();
	UtilLog4j.log.info(this, "Se encontraron " + (!itinearios.isEmpty() ? itinearios.size() : 0) + " Itinearios");
	return itinearios;
    }

    /**
     * Creo: NLopez
     *
     * @param idSolicitud
     * @return
     */
    
    public List<HistorialItinerarioVO> getHistorialItinerarioPorSolicitud(Integer idSolicitud) {

	List<HistorialItinerarioVO> itinearios = new ArrayList<HistorialItinerarioVO>();

	limpiar();

	sb.append(" Select it.ID, "); // 0
	sb.append(" ciudadOrigen.NOMBRE,"); // 1
	sb.append(" ciudadDestino.NOMBRE,  "); // 2
	sb.append(" dit.historial, "); // 3
	sb.append(" dit.numero_vuelo "); // 4
	sb.append(" FROM sg_itinerario it, sg_detalle_itinerario dit, ");
	sb.append(" SI_CIUDAD ciudadOrigen, ");
	sb.append(" SI_CIUDAD ciudadDestino ");
	sb.append(" WHERE it.sg_solicitud_viaje = ").append(idSolicitud);
	sb.append(" AND it.id = dit.sg_itinerario ");
	sb.append(" and it.SI_CIUDAD_ORIGEN = ciudadOrigen.id ");
	sb.append(" and it.SI_CIUDAD_DESTINO = ciudadDestino.id ");
	sb.append(" and dit.eliminado = 'False' ");
	sb.append(" order by dit.historial desc");
        //sb.append(" AND dit.historial = '").append(Constantes.BOOLEAN_TRUE).append("' ");

	//Asignando parámetros
	List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
	if (lo != null) {

	    for (Object[] objects : lo) {
		itinearios.add(castReturnHistorialItinerarioVO(objects));
	    }
	}

	return itinearios;
    }

    private HistorialItinerarioVO castReturnHistorialItinerarioVO(Object[] obj) {

	HistorialItinerarioVO vo = new HistorialItinerarioVO();
	vo.setId((Integer) obj[0]);
	vo.setOrigen((String) obj[1]);
	vo.setDestino((String) obj[2]);
	vo.setVigente((Boolean) obj[3]);
	vo.setNumeroVuelo((obj[4] != null) ? (String) obj[4] : "");
	return vo;
    }

    
    public boolean notificaCambioItinerario(int idSolicitud, boolean ida) {

	SolicitudViajeVO solicitudViajeVO = sgSolicitudViajeRemote.buscarPorId(idSolicitud, Constantes.NO_ELIMINADO, Constantes.CERO);
	String correoPara;
	List<ViajeroVO> ls = sgViajeroRemote.getAllViajerosList(solicitudViajeVO.getIdSolicitud());
	correoPara = correoPara(ls);
	 return notificacionViajeRemote.enviarcorreoCambioItinerario(idSolicitud, correoPara, solicitudViajeVO.getCodigo(), ida);
    }

    
    public void actualizaNotificacion(int idItinerario, String idUsuario) {
	SgItinerario itinerario = find(idItinerario);
	itinerario.setNotificado(Constantes.BOOLEAN_FALSE);
	itinerario.setModifico(new Usuario(idUsuario));
	itinerario.setFechaModifico(new Date());
	itinerario.setHoraModifico(new Date());
	edit(itinerario);
    }

    private String correoPara(List<ViajeroVO> lv) {
	String correo = "";
	for (ViajeroVO viajeroVO : lv) {
	    if (viajeroVO.getIdInvitado() == 0) {
		if (correo.isEmpty()) {
		    correo = viajeroVO.getCorreo();
		} else {
		    correo += "," + viajeroVO.getCorreo();
		}
	    }
	}
	return correo;
    }
}
