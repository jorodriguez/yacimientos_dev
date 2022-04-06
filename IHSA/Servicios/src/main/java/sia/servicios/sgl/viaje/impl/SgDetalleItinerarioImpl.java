/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.ItemUsedBySystemException;
import sia.modelo.SgDetalleItinerario;
import sia.modelo.SiCiudad;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.DetalleItinerarioCompletoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgDetalleItinerarioImpl extends AbstractFacade<SgDetalleItinerario> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Inject
    private SgItinerarioImpl sgItinerarioRemote;
    @Inject
    private SgAerolineaImpl sgAerolineaRemote;
    @Inject
    private SiCiudadImpl siCiudadRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    private StringBuilder sb = new StringBuilder();

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    private void limpiar() {
	sb.delete(0, sb.length());
    }

    public SgDetalleItinerarioImpl() {
	super(SgDetalleItinerario.class);
    }

    
    public void save(SgDetalleItinerario sgDetalleItinerario, int idSgItinerario, int idSgAerolinea, int idSiCiudadOrigen, int idSiCiudadDestino, String idUsuario) {
	UtilLog4j.log.info(this, "SgDetalleItinerarioImpl.save()");

	sgDetalleItinerario.setSgItinerario(this.sgItinerarioRemote.find(idSgItinerario));
	sgDetalleItinerario.setSgAerolinea(this.sgAerolineaRemote.find(idSgAerolinea));
	sgDetalleItinerario.setSiCiudadOrigen(this.siCiudadRemote.find(idSiCiudadOrigen));
	sgDetalleItinerario.setSiCiudadDestino(this.siCiudadRemote.find(idSiCiudadDestino));
	sgDetalleItinerario.setGenero(new Usuario(idUsuario));
	sgDetalleItinerario.setFechaGenero(new Date());
	sgDetalleItinerario.setHoraGenero(new Date());
	sgDetalleItinerario.setEliminado(Constantes.NO_ELIMINADO);
	sgDetalleItinerario.setHistorial(Constantes.BOOLEAN_FALSE);
	create(sgDetalleItinerario);	
	UtilLog4j.log.info(this, "SgDetalleItinerario CREATED SUCCESSFULLY");
    }

    
    public void save(int idSgItinerario, int idSgAerolinea, int idSiCiudadOrigen, int idSiCiudadDestino, String idUsuario, String vuelo,
	    Date fechaSalida, Calendar horaSalida, Date fechaLlegada, Calendar horaLlegada) {
	UtilLog4j.log.info(this, "SgDetalleItinerarioImpl.save()");
	SgDetalleItinerario sgDetalleItinerario = new SgDetalleItinerario();
	sgDetalleItinerario.setFechaSalida(fechaSalida);
	sgDetalleItinerario.setHoraSalida(horaSalida.getTime());
	sgDetalleItinerario.setFechaLlegada(fechaLlegada);
	sgDetalleItinerario.setHoraLlegada(horaLlegada.getTime());
	sgDetalleItinerario.setNumeroVuelo(vuelo);
	sgDetalleItinerario.setSgItinerario(sgItinerarioRemote.find(idSgItinerario));
	sgDetalleItinerario.setSgAerolinea(sgAerolineaRemote.find(idSgAerolinea));
	sgDetalleItinerario.setSiCiudadOrigen(new SiCiudad(idSiCiudadOrigen));
	sgDetalleItinerario.setSiCiudadDestino(new SiCiudad(idSiCiudadDestino));
	sgDetalleItinerario.setGenero(new Usuario(idUsuario));
	sgDetalleItinerario.setFechaGenero(new Date());
	sgDetalleItinerario.setHoraGenero(new Date());
	sgDetalleItinerario.setEliminado(Constantes.NO_ELIMINADO);
	sgDetalleItinerario.setHistorial(Constantes.BOOLEAN_FALSE);

	Long tiempoVuelo = this.siManejoFechaLocal.getDiffInMinutes(horaSalida, horaLlegada);
	Double tv = (double) (tiempoVuelo.doubleValue() / 60);
	sgDetalleItinerario.setTiempoVuelo(tv);
	create(sgDetalleItinerario);	
	UtilLog4j.log.info(this, "SgDetalleItinerario CREATED SUCCESSFULLY");
    }

    
    public void update(SgDetalleItinerario sgDetalleItinerario, int idSgItinerario, int idSgAerolinea, String idUsuario) {
	UtilLog4j.log.info(this, "SgDetalleItinerarioImpl.update()");
	sgDetalleItinerario.setSgItinerario(this.sgItinerarioRemote.find(idSgItinerario));
	sgDetalleItinerario.setSgAerolinea(this.sgAerolineaRemote.find(idSgAerolinea));
	sgDetalleItinerario.setModifico(new Usuario(idUsuario));
	sgDetalleItinerario.setFechaModifico(new Date());
	sgDetalleItinerario.setHoraModifico(new Date());

	super.edit(sgDetalleItinerario);
	UtilLog4j.log.info(this, "SgDetalleItinerario UPDATED SUCCESSFULLY");
    }

    /**
     * Cambios en
     */
    
    public void update(int idSgDetalleItinerario, int idSgItinerario, int idSgAerolinea, int idSiCiudadOrigen,
	    int idSiCiudadDestino, String numeroVuelo, Date fechaSalida, Date horaSalida, Date fechaLlegada, Date horaLlegada,
	    double tiempoVuelo, String idUsuario) {
	UtilLog4j.log.info(this, "SgDetalleItinerarioImpl.update()");

	SgDetalleItinerario sgDetalleItinerario = find(idSgDetalleItinerario);
	//Cambios
	if (sgDetalleItinerario.getSgAerolinea().getId().intValue() != idSgAerolinea
		|| sgDetalleItinerario.getSgAerolinea().getId().intValue() != idSiCiudadOrigen
		|| sgDetalleItinerario.getSgAerolinea().getId().intValue() != idSiCiudadDestino
		|| !sgDetalleItinerario.getNumeroVuelo().equals(numeroVuelo)
		|| sgDetalleItinerario.getFechaSalida() != fechaSalida
		|| sgDetalleItinerario.getHoraSalida() != horaSalida
		|| sgDetalleItinerario.getFechaLlegada() != fechaLlegada
		|| sgDetalleItinerario.getHoraLlegada() != horaLlegada) {
	    SgDetalleItinerario sdi = new SgDetalleItinerario();
	    sdi.setSgItinerario(this.sgItinerarioRemote.find(idSgItinerario));
	    sdi.setSgAerolinea(this.sgAerolineaRemote.find(idSgAerolinea));
	    sdi.setSiCiudadOrigen(this.siCiudadRemote.find(idSiCiudadOrigen));
	    sdi.setSiCiudadDestino(this.siCiudadRemote.find(idSiCiudadDestino));
	    sdi.setGenero(new Usuario(idUsuario));
	    sdi.setFechaGenero(new Date());
	    sdi.setHoraGenero(new Date());
	    sdi.setNumeroVuelo(numeroVuelo);
	    sdi.setTiempoVuelo(tiempoVuelo);
	    sdi.setFechaSalida(fechaSalida);
	    sdi.setHoraSalida(horaSalida);
	    sdi.setFechaLlegada(fechaLlegada);
	    sdi.setHoraLlegada(horaLlegada);
	    sdi.setEliminado(Constantes.BOOLEAN_FALSE);// actual
	    sdi.setHistorial(Constantes.BOOLEAN_FALSE);// actual
	    create(sdi);

            sgDetalleItinerario.setModifico(new Usuario(idUsuario));
	    sgDetalleItinerario.setFechaModifico(new Date());
	    sgDetalleItinerario.setHoraModifico(new Date());
	    sgDetalleItinerario.setHistorial(Constantes.BOOLEAN_TRUE);
	    edit(sgDetalleItinerario);
	    
	    //Actualiza el ititnerario campo notifica
	    sgItinerarioRemote.actualizaNotificacion(idSgItinerario, idUsuario);

	}
	UtilLog4j.log.info(this, "SgDetalleItinerario UPDATED SUCCESSFULLY");
    }

    
    public void delete(SgDetalleItinerario sgDetalleItinerario, String idUsuario) throws ItemUsedBySystemException {
	UtilLog4j.log.info(this, "SgDetalleItinerarioImpl.delete()");

	sgDetalleItinerario.setModifico(new Usuario(idUsuario));
	sgDetalleItinerario.setFechaModifico(new Date());
	sgDetalleItinerario.setHoraModifico(new Date());
	sgDetalleItinerario.setEliminado(Constantes.ELIMINADO);

	edit(sgDetalleItinerario);
	UtilLog4j.log.info(this, "SgDetalleItinerario DELETED SUCCESSFULLY");
    }

    
    public List<SgDetalleItinerario> findAll(String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SgDetalleItinerarioImpl.findAll()");

	List<SgDetalleItinerario> list;

	String query = "SELECT d FROM SgDetalleItinerario d WHERE d.eliminado = :eliminado";

	if (orderByField != null && !orderByField.isEmpty()) {
	    query += " ORDER BY d." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	}

	Query q = em.createQuery(query);

	//Asignando parámetros
	q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

	list = q.getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgDetalleItinerario");

	return (list != null ? list : Collections.EMPTY_LIST);
    }

//    
//    public List<SgDetalleItinerario> findBySgItinerario(SgItinerario sgItinerario, String orderByField, boolean sortAscending, boolean eliminado) {
//        UtilLog4j.log.info(this, "SgDetalleItinerarioImpl.findBySgItinerario()");
//
//        List<SgDetalleItinerario> list;
//
//        String query = "SELECT d FROM SgDetalleItinerario d WHERE d.eliminado = :eliminado AND d.sgItinerario.id = :idSgItinerario";
//
//        if (orderByField != null && !orderByField.isEmpty()) {
//            query += " ORDER BY d." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
//        }
//
//        Query q = em.createQuery(query);
//
//        //Asignando parámetros
//        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
//        q.setParameter("idSgItinerario", sgItinerario.getId());
//
//        list = q.getResultList();
//
//        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgDetalleItinerario");
//
//
//        return (list != null ? list : Collections.EMPTY_LIST);
//    }
    
    public List<DetalleItinerarioCompletoVo> traerDetalleItinerario(int idItinerario) {
	UtilLog4j.log.info(this, "### traerEstatusAprobacionPorUsuario ###");
	Query q;
	List<DetalleItinerarioCompletoVo> le = null;
	try {
	    limpiar();
	    sb.append(" Select dt.id, ");
	    sb.append(" ciudadOrigen.id as id_ciudad_origen,");
	    sb.append(" ciudadOrigen.NOMBRE as nombre_ciudad_origen,");
	    sb.append(" ciudadDestino.id as id_ciudad_destino,");
	    sb.append(" ciudadDestino.NOMBRE as nombre_ciudad_destino,");
	    sb.append(" paisOrigen.nombre as id_pais_origen,");
	    sb.append(" paisDestino.nombre as nombre_pais_origen,");
	    sb.append(" estadoOrigen.NOMBRE as id_estado_origen,");
	    sb.append(" estadoDestino.NOMBRE as nombre_estado_origen,");
	    sb.append(" dt.NUMERO_VUELO,");
	    sb.append(" ae.NOMBRE,");
	    sb.append(" dt.SG_AEROLINEA,");
	    sb.append(" dt.FECHA_SALIDA,");
	    sb.append(" dt.HORA_SALIDA,");
	    sb.append(" dt.FECHA_LLEGADA,");
	    sb.append(" dt.HORA_LLEGADA,");
	    sb.append(" dt.TIEMPO_VUELO, ");
	    sb.append(" dt.genero ");

	    sb.append(" From ");
	    sb.append(" SG_DETALLE_ITINERARIO dt,");
	    sb.append(" SG_AEROLINEA ae,");
	    sb.append(" SI_CIUDAD ciudadOrigen,");
	    sb.append(" SI_CIUDAD ciudadDestino,");
	    sb.append(" SI_PAIS paisOrigen,");
	    sb.append(" SI_PAIS paisDestino,");
	    sb.append(" SI_ESTADO estadoOrigen, ");
	    sb.append(" SI_ESTADO estadoDestino");

	    sb.append(" Where ");
	    sb.append("dt.SG_ITINERARIO = ").append(idItinerario);
	    sb.append(" and dt.SI_CIUDAD_DESTINO = ciudadDestino.id ");
	    sb.append(" and dt.SI_CIUDAD_ORIGEN = ciudadOrigen.id");
	    sb.append(" and dt.SG_AEROLINEA = ae.ID");
	    sb.append(" and ciudadOrigen.SI_PAIS = paisOrigen.id");
	    sb.append(" and ciudadDestino.SI_PAIS = paisDestino.id");
	    sb.append(" and ciudadOrigen.SI_ESTADO = estadoOrigen.id");
	    sb.append(" and ciudadDestino.SI_ESTADO = estadoDestino.id");
	    sb.append(" and dt.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    sb.append(" and (dt.HISTORIAL = '").append(Constantes.BOOLEAN_FALSE).append("'").append(" or dt.HISTORIAL IS NULL ) ");

	    List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
	    if (lo != null) {
		UtilLog4j.log.info(this, "<<<<<<<<<Existieron " + lo.size() + " escalas");
		le = new ArrayList<DetalleItinerarioCompletoVo>();
		for (Object[] objects : lo) {
		    le.add(castReturnDetalleItinerarioVO(objects));
		}
	    }
	    return le;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "excepcion en traer el detalle de itinerario " + e.getMessage());
	    return null;
	}
    }

    private DetalleItinerarioCompletoVo castReturnDetalleItinerarioVO(Object[] obj) {
	UtilLog4j.log.info(this, "castReturnDetalleItinerarioVO ");
	DetalleItinerarioCompletoVo vo = new DetalleItinerarioCompletoVo();
	vo.setId((Integer) obj[0]);
	vo.setIdCiudadOrigen((Integer) obj[1]);
	vo.setNombreCiudadOrigen((String) obj[2]);
	vo.setIdCiudadDestino((Integer) obj[3]);
	vo.setNombreCiudadDestino((String) obj[4]);
	vo.setNombrePaisOrigen((String) obj[5]);
	vo.setNombrePaisDestino((String) obj[6]);
	vo.setNombreEstadoOrigen((String) obj[7]);
	vo.setNombreEstadoDestino((String) obj[8]);
	vo.setNumeroVuelo((String) obj[9]);
	vo.setNombreAerolinea((String) obj[10]);
	vo.setIdAerolinea((Integer) obj[11]);
	vo.setFechaSalida((Date) obj[12]);
	vo.setHoraSalida((Date) obj[13]);
	vo.setFechaLlegada((Date) obj[14]);
	vo.setHoraLlegada((Date) obj[15]);
	vo.setTiempoVuelo((Double) obj[16]);
	vo.setGenero((String) obj[17]);

	return vo;
    }
}
