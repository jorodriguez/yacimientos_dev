/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.modelo.SgDetalleSolicitudEstancia;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgOficina;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgTipo;
import sia.modelo.SgViajero;
import sia.modelo.SiAdjunto;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.HuespedVo;
import sia.modelo.sgl.hotel.vo.SgHotelTipoEspecificoVo;
import sia.modelo.sgl.pago.vo.PagoServicioVo;
import sia.modelo.sgl.vo.SgHuespedHotelVo;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author marino
 */
@Stateless 
public class SgHuespedHotelImpl extends AbstractFacade<SgHuespedHotel> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaRemote;
    @Inject
    private SgHotelHabitacionImpl sgHotelHabitacionRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgHuespedHotelSiMovimientoImpl sgHuespedHotelSiMovimientoRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgHuespedHotelServicioImpl sgHuespedHotelServicioRemote;
    @Inject
    private SgHotelTipoEspecificoImpl sgHotelTipoEspecificoRemote;
    //
    private Date manana;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgHuespedHotelImpl() {
	super(SgHuespedHotel.class);
    }

    
    public void actualizar(int idSgHuespedHotelOriginal, int idSgTipoEspecifico, int idSgHotelHabitacion, String numeroReservacion, Date fechaIngreso, Date fechaSalida, boolean hospedado, boolean cancelado, String idUsuario) {

	SgHuespedHotel sgHuespedHotel = find(idSgHuespedHotelOriginal);

	sgHuespedHotel.setSgTipoEspecifico(this.sgTipoEspecificoRemote.find(idSgTipoEspecifico));
	sgHuespedHotel.setSgHotelHabitacion(this.sgHotelHabitacionRemote.find(idSgHotelHabitacion));
	sgHuespedHotel.setNumeroHabitacion(numeroReservacion);
	sgHuespedHotel.setFechaIngreso(fechaIngreso);
	sgHuespedHotel.setFechaSalida(fechaSalida);
	sgHuespedHotel.setHospedado(hospedado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
	sgHuespedHotel.setCancelado(cancelado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
	sgHuespedHotel.setModifico(new Usuario(idUsuario));
	sgHuespedHotel.setFechaModifico(new Date());
	sgHuespedHotel.setHoraModifico(new Date());

	edit(sgHuespedHotel);
	UtilLog4j.log.debug(this, "SgOficina UPDATED SUCCESSFULLY");
    }

    
    public void actualizarFechaSalida(int idSgHuespedHotel, Date fechaNuevaDeSalida, String idUsuario) {
	SgHuespedHotel hh = find(idSgHuespedHotel);
	if (notificacionServiciosGeneralesRemote.enviarCorreoRegistroProlongadoPorSemaforoHuespedHotel(hh, idUsuario)) {
	    hh.setFechaSalida(fechaNuevaDeSalida);
	    hh.setFechaModifico(new Date());
	    hh.setHoraModifico(new Date());
	    hh.setModifico(new Usuario(idUsuario));

	    edit(hh);
	}
    }

    
    public boolean crearNuevoRegistroEstanciaProlongada(int idHuespedhotel, Date nuevaFecha, String idUsuario) {
	escribirLog("crearNuevoRegistroEstanciaProlongada " + idHuespedhotel);
	try {
	    SgHuespedHotel sgHuespedHotelOriginal = find(idHuespedhotel);
	    //&& (sgHuespedHotelOriginal.getProlongada().equals(true))
	    if (sgHuespedHotelOriginal.getSiAdjunto() == null) {
		escribirLog("el registro original es prolongado se cambiara por que no tiene adjunto ");
		//no se le ha agredado una carta de asignacion y sale hoy el huesped
		prolongarFechaHuesped(sgHuespedHotelOriginal, nuevaFecha, idUsuario);
	    } else {
		escribirLog("el registro original No se usara,,,, comenzando a buscar registro prolongados del original ");
		//buscar registro igual a el
		HuespedVo vo = this.buscarRegistroHuespedConSalidaMañanaOMayorAMañana(sgHuespedHotelOriginal);
		if (vo != null) {//si existe un registro prolongado..
		    escribirLog("Se ha encontrado un registro prolongado  ");
		    //checar si tiene adjunto
		    if (vo.getIdAdjunto() == null) {
			escribirLog("Este registro tiene  NO tiene adjunto se procede a modificar la fecha de salida  ");
			//find al registro encontrado
			SgHuespedHotel h = find(vo.getId());
			prolongarFechaHuesped(h, nuevaFecha, idUsuario);
		    } else { // No tiene adjunto...crear unno nuevo como el original
			escribirLog("Este registro tiene  adjunto, comezando a crear un nuevo registro");
			crearNuevoRegistroProlongadoEstancia(sgHuespedHotelOriginal, nuevaFecha, idUsuario);
//                        salidaHuespedHotelForTimer(sgHuespedHotelOriginal.getId(),(esTimer ? Constantes.USUARIO_SIA:idUsuario));
		    }
		} else {
		    crearNuevoRegistroProlongadoEstancia(sgHuespedHotelOriginal, nuevaFecha, idUsuario);
//                    salidaHuespedHotelForTimer(sgHuespedHotelOriginal.getId(),(esTimer ? Constantes.USUARIO_SIA:idUsuario));
		}
	    }
	    return true;
	} catch (Exception e) {
	    escribirLog("Excepcion al crear nueva instancia de un huespedHotel " + e.getMessage());
	    return false;
	}
    }

    private boolean prolongarFechaHuesped(SgHuespedHotel sgHuespedHotelOriginal, Date nuevaFecha, String idUsuario) {
	sgHuespedHotelOriginal.setFechaSalida(nuevaFecha);
	sgHuespedHotelOriginal.setProlongada(Constantes.BOOLEAN_TRUE);
	sgHuespedHotelOriginal.setModifico(new Usuario(idUsuario));
	sgHuespedHotelOriginal.setFechaModifico(new Date());
	sgHuespedHotelOriginal.setHoraModifico(new Date());
	//enviar correo avisando que se ha prolongado la fecha del registro
	if (notificacionServiciosGeneralesRemote.enviarCorreoRegistroProlongadoPorSemaforoHuespedHotel(sgHuespedHotelOriginal, idUsuario)) {
	    edit(sgHuespedHotelOriginal);
	    return true;
	} else {
	    return false;
	}
    }

    private boolean crearNuevoRegistroProlongadoEstancia(SgHuespedHotel sgHuespedHotelOriginal, Date nuevaFecha, String idUsuario) {
	escribirLog("Creando un nuevo registro prolongado paa el huesped " + sgHuespedHotelOriginal.getId());
	SgHuespedHotel sgHuespedHotelProlongado = new SgHuespedHotel();
	/*
	 * poner en un metodo para clonar registro..*
	 */
	escribirLog("Detalle de soli i" + (sgHuespedHotelOriginal.getSgDetalleSolicitudEstancia() != null ? sgHuespedHotelOriginal.getSgDetalleSolicitudEstancia().getId() : " ES NULLLLLLLLLLLLL"));
	sgHuespedHotelProlongado.setHospedado(Constantes.BOOLEAN_TRUE);
	sgHuespedHotelProlongado.setCancelado(Constantes.BOOLEAN_FALSE);
	sgHuespedHotelProlongado.setSgDetalleSolicitudEstancia(sgHuespedHotelOriginal.getSgDetalleSolicitudEstancia());
	sgHuespedHotelProlongado.setSgHotelHabitacion(sgHuespedHotelOriginal.getSgHotelHabitacion());
	sgHuespedHotelProlongado.setSgSolicitudEstancia(sgHuespedHotelOriginal.getSgSolicitudEstancia());
	sgHuespedHotelProlongado.setSgTipo(sgHuespedHotelOriginal.getSgTipo());
	sgHuespedHotelProlongado.setSgTipoEspecifico(sgHuespedHotelOriginal.getSgTipoEspecifico());
	sgHuespedHotelProlongado.setFechaIngreso(sgHuespedHotelOriginal.getFechaSalida());
	sgHuespedHotelProlongado.setFechaSalida(nuevaFecha);
	sgHuespedHotelProlongado.setGenero(new Usuario(Constantes.USUARIO_SIA));
	sgHuespedHotelProlongado.setFechaGenero(new Date());
	sgHuespedHotelProlongado.setHoraGenero(new Date());
	sgHuespedHotelProlongado.setEliminado(Constantes.NO_ELIMINADO);
	sgHuespedHotelProlongado.setProlongada(Constantes.BOOLEAN_TRUE);

	/*
	 * hasta aqui
	 */
	escribirLog("antes de guardar: ");
	escribirLog(sgHuespedHotelProlongado.toString());
	//enviar correo para avisar que se ha creado un nuevo registro de estancia
	if (notificacionServiciosGeneralesRemote.enviarCorreoRegistroProlongadoPorSemaforoHuespedHotel(sgHuespedHotelOriginal, idUsuario)) {
	    create(sgHuespedHotelProlongado);

	    UtilLog4j.log.info(this, "SgHuespedHotel CREATED SUCCESSFULLY");

	    //Movimiento
	    SiMovimiento siMovimiento = this.siMovimientoRemote.save(Constantes.MOTIVO_PROLONGACION_ESTANCIA, Constantes.ID_SI_OPERACION_PROLONGAR_ESTANCIA, idUsuario);

	    if (siMovimiento != null) {
		this.sgHuespedHotelSiMovimientoRemote.save(sgHuespedHotelOriginal.getId().intValue(), siMovimiento.getId().intValue(), idUsuario);
	    }
	    return true;
	} else {
	    return false;
	}
    }

    private HuespedVo buscarRegistroHuespedConSalidaMañanaOMayorAMañana(SgHuespedHotel huespedHotel) {
	HuespedVo vo = null;
	try {
	    clearQuery();
	    appendQuery(" select hh.id, ");//0
	    appendQuery(" hh.FECHA_INGRESO,");//1
	    appendQuery(" hh.FECHA_SALIDA,");//2
	    appendQuery(" hh.HOSPEDADO,");//3
	    appendQuery(" hh.NUMERO_HABITACION,");//4
	    appendQuery(" hh.SG_DETALLE_SOLICITUD_ESTANCIA,");//5
	    appendQuery(" hh.SG_SOLICITUD_ESTANCIA,");//6
	    //appendQuery(" hh.SG_TIPO,");//7
	    appendQuery(" hh.SG_TIPO_ESPECIFICO,");//7
	    appendQuery(" hh.SI_ADJUNTO       ");//8
	    appendQuery(" from SG_HUESPED_HOTEL hh,SG_HOTEL_HABITACION hotel");
	    appendQuery(" where hh.SG_DETALLE_SOLICITUD_ESTANCIA = ");
	    appendQuery(huespedHotel.getSgDetalleSolicitudEstancia().getId());
	    appendQuery(" and hh.SG_SOLICITUD_ESTANCIA = ");
	    appendQuery(huespedHotel.getSgSolicitudEstancia().getId());
	    appendQuery(" and hh.ELIMINADO = 'False'");
	    appendQuery(" and hotel.SG_HOTEL = ");
	    appendQuery(huespedHotel.getSgHotelHabitacion().getSgHotel().getId());
	    appendQuery(" and hh.HOSPEDADO = 'True'");
	    appendQuery(" and hh.SG_HOTEL_HABITACION = hotel.ID");
	    appendQuery(" and (hh.FECHA_SALIDA >= dateadd(1 day to cast('now' as Date)))");

	    Object[] object = (Object[]) em.createNativeQuery(getStringQuery()).getSingleResult();

	    if (object != null) {
		vo = new HuespedVo();
		vo.setId((Integer) object[0]);
		vo.setFechaIngreso((Date) object[1]);
		vo.setFechaSalida((Date) object[2]);
		vo.setHospedado(((String) object[3]).equals(true) ? true : false);
		vo.setNumeroHabitacionHotel((String) object[4]);
		vo.setIdSgDetalleSolicitudEstancia((Integer) object[5]);
		vo.setIdSgSolicitudEstancia((Integer) object[6]);
		vo.setIdSgTipoEspecifico((Integer) object[7]);
		vo.setIdAdjunto((Integer) object[8]);
	    }
	    return vo;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al buscar un registro PorProlongar ");
	    return null;
	}

    }

    
    public boolean guardarHuespedHotel(String usuario, SgHuespedHotel sgHuespedHotel, int idDetalleEstancia, int idInvitado, String invitado, String empleado, String tipoEspecifico, String correoEmpleado, int idHotel,
	    int idHabitacion, int idSolicitudEstancia, SgTipo sgTipo, int idTipoEspecifico, String estancia) {
	UtilLog4j.log.debug(this, "SgHuespedHotelImpl.guardarHuespedHotel()");
	UtilLog4j.log.info(this, "SgHuespedHotelImpl.guardarHuespedHotel()");

	boolean v = false;
	try {

	    v = notificacionServiciosGeneralesRemote.enviarCorreoRegistroHuespedHotel(sgHuespedHotel, estancia, idDetalleEstancia, idInvitado,
		    invitado, empleado, tipoEspecifico, correoEmpleado, idSolicitudEstancia, idHotel, idTipoEspecifico);
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    UtilLog4j.log.fatal(this, "excepction " + e.getMessage());
	}
	try {
	    int c = 0;
	    if (v) {
		//Actualiza el detalle de la solicitud
		SgDetalleSolicitudEstancia sdse = sgDetalleSolicitudEstanciaRemote.find(idDetalleEstancia);
		sdse.setRegistrado(Constantes.BOOLEAN_TRUE);
		sgDetalleSolicitudEstanciaRemote.edit(sdse);
		//Actualiza la solicitud
		//
		List<DetalleEstanciaVO> lista = sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(idSolicitudEstancia, Constantes.NO_ELIMINADO);
		for (DetalleEstanciaVO sgDetSol : lista) {
		    if (!sgDetSol.isRegistrado() && !sgDetSol.isCancelado()) {
			c++;
		    }
		}
		if (c == 0) {
		    sgSolicitudEstanciaRemote.finalizaSolicitud(idSolicitudEstancia, usuario);
		}

		//     SgTipoEspecifico tipoEspecifico = sgTipoEspecificoRemote.find(idTipoEspecifico);
		//Crea el registro huésped
		sgHuespedHotel.setSgTipo(sgTipo);
		sgHuespedHotel.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idTipoEspecifico));
		sgHuespedHotel.setSgHotelHabitacion(sgHotelHabitacionRemote.find(idHabitacion));
		sgHuespedHotel.setSgDetalleSolicitudEstancia(sgDetalleSolicitudEstanciaRemote.find(idDetalleEstancia));
		sgHuespedHotel.setSgSolicitudEstancia(sgSolicitudEstanciaRemote.find(idSolicitudEstancia));
		sgHuespedHotel.setHospedado(Constantes.BOOLEAN_TRUE);
		sgHuespedHotel.setGenero(new Usuario(usuario));
		sgHuespedHotel.setFechaGenero(new Date());
		sgHuespedHotel.setHoraGenero(new Date());
		sgHuespedHotel.setCancelado(Constantes.BOOLEAN_FALSE);
		sgHuespedHotel.setEliminado(Constantes.NO_ELIMINADO);
		sgHuespedHotel.setProlongada(Constantes.BOOLEAN_FALSE);
		create(sgHuespedHotel);

	    }
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    UtilLog4j.log.fatal(this, "excepccion  " + e.getMessage());
	}
	return v;
    }

    
    public List<SgHuespedHotel> traerHuespedPorHotel(int idHotel, String idSesion) {
	UtilLog4j.log.debug(this, "SgHuespedHotel.traerHuespedPorHotel()");

	try {
	    return em.createQuery("SELECT h FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.id = :idHotel "
		    + " AND h.hospedado = :hospe "
		    //                    + " AND h.sgSolicitudEstancia.usuarioHospeda.id = :idHospeda "
		    + " AND h.cancelado = :can "
		    + " AND h.eliminado = :eli "
		    + " ORDER BY h.id ASC ").setParameter("idHotel", idHotel).setParameter("hospe", Constantes.BOOLEAN_TRUE).setParameter("can", Constantes.BOOLEAN_FALSE).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public void marcarSalidaHuesped(Usuario usuario, SgHuespedHotel sgHuespedHotel) {
	UtilLog4j.log.debug(this, "marcarSalidaHuesped");

	boolean v;
	try {	    
	    sgHuespedHotel.setModifico(usuario);
	    sgHuespedHotel.setFechaModifico(new Date());
	    sgHuespedHotel.setHoraModifico(new Date());
	    sgHuespedHotel.setFechaSalida(new Date());
	    sgHuespedHotel.setHospedado(Constantes.BOOLEAN_FALSE);
	    sgHuespedHotel.setFechaRealSalida(sgHuespedHotel.getFechaRealSalida() == null ? (new Date()) : sgHuespedHotel.getFechaRealSalida());
	    sgHuespedHotel.setFechaRealIngreso(sgHuespedHotel.getFechaRealIngreso() == null ? (new Date()) : sgHuespedHotel.getFechaRealIngreso());
	    v = notificacionServiciosGeneralesRemote.enviaCorreoSalidaHusped(usuario, sgHuespedHotel);
	    if (v) {
		edit(sgHuespedHotel);		
	    }
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Excepción en marcarSalidaHuesped ", e);
	}
    }

    
    public void salidaHuespedHotelForTimer(int idSgHuespedHotel, String idUsario) {
	escribirLog(" Comenzando a sacar el huesped en el hotel......... " + idSgHuespedHotel);
	boolean sentMail;
	SgHuespedHotel sgHuespedHotel = find(idSgHuespedHotel);	

	sgHuespedHotel.setModifico(new Usuario(idUsario));
	sgHuespedHotel.setFechaModifico(new Date());
	sgHuespedHotel.setHoraModifico(new Date());
	sgHuespedHotel.setFechaSalida(new Date());
	sgHuespedHotel.setHospedado(Constantes.BOOLEAN_FALSE);
	sgHuespedHotel.setFechaRealSalida(sgHuespedHotel.getFechaRealSalida() == null ? (new Date()) : sgHuespedHotel.getFechaRealSalida());
	sgHuespedHotel.setFechaRealIngreso(sgHuespedHotel.getFechaRealIngreso() == null ? (new Date()) : sgHuespedHotel.getFechaRealIngreso());

	try {
	    sentMail = notificacionServiciosGeneralesRemote.enviaCorreoSalidaHusped(this.usuarioRemote.find(idUsario), sgHuespedHotel);
	    if (sentMail) {
		edit(sgHuespedHotel);
	    } else {
		//Método de pruebas
////                this.notificacionServiciosGeneralesRemote.sendNotificacionForTeamSIA(
//                        "Hubo un error al sacar por el timer al huésped " + (sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre()) + " del hotel " + sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre(),
//                        "Error - Huéspedes Hotel");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
//            this.notificacionServiciosGeneralesRemote.sendNotificacionForTeamSIA(
//                    ("Hubo un error al sacar por el timer al huésped " + (sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : sgHuespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre()) + " del hotel " + sgHuespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre()),
//                    "Error - Huéspedes Hotel");
	    UtilLog4j.log.error(this, e.getMessage(), e);
	}
    }

    
    public void cancelarRegistroHuesped(Usuario usuario, SgHuespedHotel sgHuespedHotel) {
	boolean v;	
	sgHuespedHotel.setGenero(usuario);
	sgHuespedHotel.setFechaModifico(new Date());
	sgHuespedHotel.setFechaSalida(new Date());
	sgHuespedHotel.setHoraModifico(new Date());
	sgHuespedHotel.setHospedado(Constantes.BOOLEAN_FALSE);
	sgHuespedHotel.setCancelado(Constantes.BOOLEAN_TRUE);
	v = notificacionServiciosGeneralesRemote.enviaCorreoCancelaRegistroHuesped(usuario, sgHuespedHotel);
	if (v) {
	    edit(sgHuespedHotel);
	}
    }

    
    public void eliminarRegistroHuesped(Usuario usuario, SgHuespedHotel sgHuespedHotel) {
	try {
	    sgHuespedHotel.setModifico(usuario);
	    sgHuespedHotel.setFechaModifico(new Date());
	    sgHuespedHotel.setHoraModifico(new Date());
	    sgHuespedHotel.setHospedado(Constantes.BOOLEAN_FALSE);
	    sgHuespedHotel.setEliminado(Constantes.ELIMINADO);
	    edit(sgHuespedHotel);	    
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Excepción en eliminarRegistroHuesped ", e);
	}
    }

    
    public boolean registrarCambioHuespedHotel(Usuario usuario, SgHuespedHotel sgHuespedHotel, SgHuespedHotel sgHuespedHotelSeleccionado,
	    SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia, int idHotel,
	    int idHabitacion, SgSolicitudEstancia idSolicitudEstancia, SgTipo sgTipo, int idTipoEspecifico) {
	boolean v = false;
	try {
	    v = notificacionServiciosGeneralesRemote.enviarCorreoRegistroHuespedHotel(sgHuespedHotelSeleccionado, "",
		    sgDetalleSolicitudEstancia.getId(), sgDetalleSolicitudEstancia.getSgInvitado() != null ? sgDetalleSolicitudEstancia.getSgInvitado().getId() : 0,
		    sgDetalleSolicitudEstancia.getSgInvitado() != null ? sgDetalleSolicitudEstancia.getSgInvitado().getNombre() : "",
		    sgDetalleSolicitudEstancia.getUsuario() != null ? sgDetalleSolicitudEstancia.getUsuario().getNombre() : "",
		    sgDetalleSolicitudEstancia.getSgTipoEspecifico().getNombre(),
		    sgDetalleSolicitudEstancia.getUsuario() != null ? sgDetalleSolicitudEstancia.getUsuario().getEmail() : "", idSolicitudEstancia.getId(), idHotel, idTipoEspecifico);
	    //Se moficica el registro anterior
	    if (v) {
		sgHuespedHotel.setModifico(usuario);
		sgHuespedHotel.setFechaModifico(new Date());
		sgHuespedHotel.setHoraModifico(new Date());
		sgHuespedHotel.setHospedado(Constantes.BOOLEAN_FALSE);
		sgHuespedHotel.setCancelado(Constantes.BOOLEAN_FALSE);
		edit(sgHuespedHotel);
		
		//Crea el nuevo registro huésped
		sgHuespedHotelSeleccionado.setSgTipo(sgTipo);
		sgHuespedHotelSeleccionado.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idTipoEspecifico));
		sgHuespedHotelSeleccionado.setSgHotelHabitacion(sgHotelHabitacionRemote.find(idHabitacion));
		sgHuespedHotelSeleccionado.setSgDetalleSolicitudEstancia(sgDetalleSolicitudEstancia);
		sgHuespedHotelSeleccionado.setSgSolicitudEstancia(idSolicitudEstancia);
		sgHuespedHotelSeleccionado.setHospedado(Constantes.BOOLEAN_TRUE);
		sgHuespedHotelSeleccionado.setGenero(usuario);
		sgHuespedHotelSeleccionado.setFechaGenero(new Date());
		sgHuespedHotelSeleccionado.setHoraGenero(new Date());
		sgHuespedHotelSeleccionado.setCancelado(Constantes.BOOLEAN_FALSE);
		sgHuespedHotelSeleccionado.setEliminado(Constantes.NO_ELIMINADO);

		create(sgHuespedHotelSeleccionado);
		
                //Guardar los servicios proveídos por el hotel
		List<SgHotelTipoEspecificoVo> serviciosHotel = this.sgHotelTipoEspecificoRemote.getAllSgHotelTipoEspecificoBySgHotelAndProvided(idHotel, true, "id", true, false);

		if (serviciosHotel != null && !serviciosHotel.isEmpty()) {
		    for (SgHotelTipoEspecificoVo vo : serviciosHotel) {
			this.sgHuespedHotelServicioRemote.save(sgHuespedHotelSeleccionado.getId().intValue(), vo.getIdSgTipoEspecifico(), false, usuario.getId());
		    }
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Dentro de la excepción, porque es un lugar donde alguna vez debes de estar", e);
	}
	return v;
    }

    
    public void guardarCartaAsignacion(Usuario usuario, SgHuespedHotel sgHuespedHotel, SiAdjunto siAdjunto) {
	try {
	    sgHuespedHotel.setModifico(usuario);
	    sgHuespedHotel.setFechaModifico(new Date());
	    sgHuespedHotel.setHoraModifico(new Date());
	    sgHuespedHotel.setSiAdjunto(siAdjunto);

	    edit(sgHuespedHotel);

	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Excepción en cancelarRegistroHuesped ", e);
	}
    }

    
    public boolean quitarCartaAsignacion(Usuario usuario, SgHuespedHotel sgHuespedHotel) {
	boolean v;
	try {
	    sgHuespedHotel.setSiAdjunto(null);
	    sgHuespedHotel.setModifico(usuario);
	    sgHuespedHotel.setFechaModifico(new Date());
	    sgHuespedHotel.setHoraModifico(new Date());

	    edit(sgHuespedHotel);

	    v = true;
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    v = false;
	}
	return v;
    }

    
    public List<SgHuespedHotel> traerHospedadosHotel(int sgSolicitudEstancia) {
	try {
	    return em.createQuery("SELECT h FROM SgHuespedHotel h WHERE h.sgSolicitudEstancia.id = :idSol "
		    + " AND h.hospedado = :hos "
		    + " AND h.eliminado = :eli "
		    + " ORDER BY h.id ASC").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("idSol", sgSolicitudEstancia).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public List<SgHuespedHotel> traerCanceladosHotel(SgSolicitudEstancia sgSolicitudEstancia) {
	try {
	    return em.createQuery("SELECT h FROM SgHuespedHotel h WHERE h.sgSolicitudEstancia.id = :idSol "
		    + " AND h.cancelado = :can "
		    + " AND h.eliminado = :eli "
		    + " ORDER BY h.id ASC").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("idSol", sgSolicitudEstancia.getId()).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public List<SgHuespedHotel> traerNoHospedadosNoCanceladosHotel(SgSolicitudEstancia sgSolicitudEstancia) {
	try {
	    return em.createQuery("SELECT h FROM SgHuespedHotel h WHERE "
		    + " h.sgSolicitudEstancia.id = :idSol "
		    + " AND h.hospedado = :hos "
		    + " AND h.eliminado = :eli "
		    + " AND h.cancelado = :can"
		    + " ORDER BY h.id ASC").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_FALSE).setParameter("can", Constantes.BOOLEAN_FALSE).setParameter("idSol", sgSolicitudEstancia.getId()).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }
    //para sacar los totales

    
    public TreeSet<Integer> traerOficina() {
	TreeSet<Integer> ts = new TreeSet<Integer>();
	try {
	    for (SgOficina sgOficina : sgOficinaRemote.findAll(false)) {
		if (!sgOficina.isEliminado() && sgOficina.isVistoBueno()) {
		    ts.add(sgOficina.getId());
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio una excepcion " + e.getStackTrace());
	    return null;
	}
	return ts;
    }

    
    public TreeSet<Integer> totalHospedadosHotel() {
	TreeSet<Integer> ts = new TreeSet<Integer>();
	try {
	    List<SgOficina> lh = sgOficinaRemote.findAll(false);
	    UtilLog4j.log.info(this, "LH: " + lh.size());
	    for (SgOficina sgOficina : lh) {
		if (!sgOficina.isEliminado() && sgOficina.isVistoBueno()) {
		    ts.add(sgOficina.getId());
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Ocurrio una excepcion ", e);
	    return null;
	}
	return ts;
    }

    
    public TreeSet<Integer> totalSalidaHotel() {
	TreeSet<Integer> ts = new TreeSet<Integer>();
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    manana = siManejoFechaLocal.fechaSumarDias(new Date(), 1);
	    UtilLog4j.log.info(this, "Manana ES: " + sdf.format(manana));
	    List<SgOficina> lh = em.createQuery("select h.sgHotelHabitacion.sgHotel.sgOficina FROM SgHuespedHotel h WHERE h.hospedado = :t "
		    + " AND  h.fechaSalida = :date"
		    + " AND  h.eliminado = :eli").setParameter("t", Constantes.BOOLEAN_TRUE).setParameter("date", manana).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	    UtilLog4j.log.info(this, "LH: " + lh.size());
	    for (SgOficina sgOficina : lh) {
		ts.add(sgOficina.getId());
	    }
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Ocurrio una excepcion ", e);
	    return null;
	}
	return ts;
    }

    
    public TreeSet<Integer> totalCanceladoHotel() {
	TreeSet<Integer> ts = new TreeSet<Integer>();
	try {
	    List<SgOficina> lh = em.createQuery("select h.sgHotelHabitacion.sgHotel.sgOficina FROM SgHuespedHotel h WHERE "
		    + " h.fechaModifico = :date"
		    + " AND  h.eliminado = :eli"
		    + " AND h.cancelado = :can").setParameter("date", new Date()).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("can", Constantes.BOOLEAN_TRUE).getResultList();
	    UtilLog4j.log.info(this, "LH: " + lh.size());
	    for (SgOficina sgOficina : lh) {
		ts.add(sgOficina.getId());
	    }
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Ocurrio una excepcion ", e);
	    return null;
	}
	return ts;
    }

//Totales huespedes -------------------------------------------------
    
    public int totalHuespedadosHotelPorOficina(int oficina) {
	return ((Long) em.createQuery("select count(h) FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.sgOficina.id = :ofi "
		+ " AND h.hospedado = :hospedado"
		+ " AND h.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hospedado", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina).getSingleResult()).intValue();
    }

    
    public int totalSalidaHotelPorOficina(int oficina) {
	return ((Long) em.createQuery("select count(h) FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.sgOficina.id = :ofi "
		+ " AND  h.fechaSalida = :date"
		+ " AND  h.eliminado = :eli"
		+ " AND  h.hospedado = :hos").setParameter("date", new Date()).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina).getSingleResult()).intValue();
    }

    
    public int totalCanceladoHotelPorOficina(int oficina) {
	return ((Long) em.createQuery("select count(h) FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.sgOficina.id = :ofi "
		+ " AND h.fechaModifico = :date"
		+ " AND h.eliminado = :eli"
		+ " AND h.cancelado = :can").setParameter("date", new Date()).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina).getSingleResult()).intValue();
    }

    
    public List<SgHuespedHotel> traerHospedadosHotelPorOficina(SgOficina oficina) {
	try {
	    return em.createQuery("SELECT h FROM SgHuespedHotel h WHERE "
		    + " h.sgHotelHabitacion.sgHotel.sgOficina.id = :ofi "
		    + " AND h.hospedado = :hos "
		    + " AND h.eliminado = :eli "
		    + " AND h.cancelado = :can"
		    + " ORDER BY h.id ASC").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("can", Constantes.BOOLEAN_FALSE).setParameter("ofi", oficina.getId()).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public List<SgHuespedHotel> traerSalidaHotelPorOficina(SgOficina oficina) {
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    manana = siManejoFechaLocal.fechaSumarDias(new Date(), 1);
	    UtilLog4j.log.info(this, "Manana ES: " + sdf.format(manana));
	    return em.createQuery("select h FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.sgOficina.id = :ofi "
		    + " AND  h.fechaSalida = :date"
		    + " AND  h.eliminado = :eli"
		    + " AND  h.hospedado = :hos").setParameter("date", manana).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina.getId()).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public List<SgHuespedHotel> traerCanceladoHotelPorOficina(SgOficina oficina) {
	try {
	    return em.createQuery("select h FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.sgOficina.id = :ofi "
		    + " AND h.fechaModifico = :date"
		    + " AND h.eliminado = :eli"
		    + " AND h.cancelado = :can").setParameter("date", new Date()).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina.getId()).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public List<SgHuespedHotel> traerSalidaPorHotel(String idHotel) {
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    manana = siManejoFechaLocal.fechaSumarDias(new Date(), 1);
	    UtilLog4j.log.info(this, "Manana ES: " + sdf.format(manana));
	    return em.createQuery("select h FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.proveedor.nombre = :hot "
		    + " AND  h.fechaSalida = :date"
		    + " AND  h.eliminado = :eli"
		    + " AND  h.hospedado = :hos").setParameter("date", manana).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("hot", idHotel).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public List<SgHuespedHotel> traerCanceladoPorHotel(String idHotel) {
	try {
	    return em.createQuery("select h FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.proveedor.nombre = :hotel "
		    + " AND h.fechaModifico = :date"
		    + " AND h.eliminado = :eli"
		    + " AND h.cancelado = :can").setParameter("date", new Date()).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("hotel", idHotel).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    //Hospedados hotel
    
    public List traerHospedadosNativo() {
	Query q = em.createNativeQuery("SELECT d.ciudad, p.nombre, count(*) "
		+ "from sg_huesped_hotel h, sg_solicitud_estancia s, sg_hotel_habitacion hh, sg_hotel o, proveedor p, sg_oficina ci, sg_direccion d"
		+ " where h.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.cancelado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.hospedado = '" + Constantes.BOOLEAN_TRUE + "'"
		+ " AND h.sg_solicitud_estancia = s.id"
		+ " AND h.sg_hotel_habitacion = hh.id"
		+ " AND hh.sg_hotel = o.id"
		+ " AND o.proveedor = p.id"
		+ " AND o.sg_oficina = ci.id"
		+ " AND ci.sg_direccion = d.id"
		+ " group by d.ciudad, p.nombre having count(*) > 0");

	UtilLog4j.log.debug(this, new StringBuilder().append("Q Hospedados hotek: ").append(q.toString()).toString());
	return q.getResultList();
    }

    //Saida hotel
    
    public List traerSalidaHuespedNativo() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	manana = siManejoFechaLocal.fechaSumarDias(new Date(), 1);
	UtilLog4j.log.info(this, "Manana ES: " + sdf.format(manana));
	Query q = em.createNativeQuery("SELECT d.ciudad, p.nombre, count(*) "
		+ " from sg_huesped_hotel h,sg_solicitud_estancia s, sg_hotel_habitacion hh, sg_hotel o, proveedor p, sg_oficina ci, sg_direccion d"
		+ " where h.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.cancelado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.hospedado = '" + Constantes.BOOLEAN_TRUE + "'"
		+ " AND h.fecha_Salida = cast('" + sdf.format(manana) + "' as date)"
		+ " AND h.sg_solicitud_estancia = s.id"
		+ " AND h.sg_hotel_habitacion = hh.id"
		+ " AND hh.sg_hotel = o.id"
		//////                + " ANd ci.id = " + idOficina
		+ " AND o.proveedor = p.id"
		+ " AND o.sg_oficina = ci.id"
		+ " AND ci.sg_direccion = d.id"
		+ " group by d.ciudad, p.nombre having count(*) > 0");

	UtilLog4j.log.debug(this, new StringBuilder().append("Q salida hotel: ").append(q.toString()).toString());
	return q.getResultList();
    }
    //Saida hotel

    
    public List traerCanceladoHuespedNativo() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	Query q = em.createNativeQuery("SELECT d.ciudad, p.nombre, count(*) "
		+ " from sg_huesped_hotel h,sg_solicitud_estancia s, sg_hotel_habitacion hh, sg_hotel o, proveedor p, sg_oficina ci, sg_direccion d"
		+ " where h.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.cancelado = '" + Constantes.BOOLEAN_TRUE + "'"
		+ " AND h.hospedado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.fecha_Salida = cast('" + sdf.format(new Date()) + "' as date)"
		+ " AND h.sg_solicitud_estancia = s.id"
		+ " AND h.sg_hotel_habitacion = hh.id"
		+ " AND hh.sg_hotel = o.id"
		////////                + " ANd ci.id = " + idOficina
		+ " AND o.proveedor = p.id"
		+ " AND o.sg_oficina = ci.id"
		+ " AND ci.sg_direccion = d.id"
		+ " group by d.ciudad, p.nombre having count(*) > 0");

	UtilLog4j.log.debug(this, new StringBuilder().append("Q cancelados hotek: ").append(q.toString()).toString());
	return q.getResultList();
    }

    //
    
    public List<SgHuespedHotel> traerHuespedadosPorHotel(String proveedor) {
	try {
	    return em.createQuery("SELECT h FROM SgHuespedHotel h WHERE h.sgHotelHabitacion.sgHotel.proveedor.nombre = :nombre "
		    + " AND h.hospedado = :hospe "
		    + " AND h.cancelado = :can "
		    + " AND h.eliminado = :eli "
		    + " ORDER BY h.id ASC ").setParameter("nombre", proveedor).setParameter("hospe", Constantes.BOOLEAN_TRUE).setParameter("can", Constantes.BOOLEAN_FALSE).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return null;
	}
    }

    
    public List llenarHospedados() {
	Query q = em.createNativeQuery("SELECT d.ciudad, p.nombre, count(*) "
		+ "from sg_huesped_hotel h, sg_solicitud_estancia s, sg_hotel_habitacion hh, sg_hotel o, proveedor p, sg_oficina ci, sg_direccion d"
		+ " where h.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.cancelado = '" + Constantes.BOOLEAN_FALSE + "'"
		+ " AND h.hospedado = '" + Constantes.BOOLEAN_TRUE + "'"
		+ " AND h.sg_solicitud_estancia = s.id"
		+ " AND h.sg_hotel_habitacion = hh.id"
		+ " AND hh.sg_hotel = o.id"
		+ " AND o.proveedor = p.id"
		+ " AND o.sg_oficina = ci.id"
		+ " AND ci.sg_direccion = d.id"
		+ " group by d.ciudad, p.nombre having count(*) > 0");

	UtilLog4j.log.debug(this, new StringBuilder().append("Q Hospedados hotek: ").append(q.toString()).toString());
	return q.getResultList();
    }

    
    public boolean cancelLoungueHotelOfTraveler(SgViajero viajero, String motivoCancelacion, Usuario usuario) {
	boolean ret = false;
	try {
	    SgHuespedHotel huespedHotel = findHuespedHotelToViajero(viajero);
	    //buscar el registro de el viajero
	    if (huespedHotel != null) {
		cancelarRegistroHuesped(usuario, huespedHotel);
		ret = true;
		//GUARDAR EN SI_MOVIMIENTO
		SiMovimiento simo = this.siMovimientoRemote.guardarSiMovimiento(motivoCancelacion, siOperacionRemote.find(3), usuario);
		if (simo != null) {
		    this.sgHuespedHotelSiMovimientoRemote.guardarHuespedHotelSiMovimiento(huespedHotel, simo, usuario);
		}
	    }
	    return ret;
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "Excepcion.. al acancelar el registro de hotel", e);
	    return false;
	}
    }

    private SgHuespedHotel findHuespedHotelToViajero(SgViajero viajero) {
	List<SgHuespedHotel> re = null;
	try {
	    //saber si es un usuario o un invitado

	    re = em.createQuery("SELECT h FROM SgHuespedHotel h"
		    + " WHERE "
		    + (viajero.getUsuario() != null ? "h.sgDetalleSolicitudEstancia.usuario.id = :idViajero " : "h.sgDetalleSolicitudEstancia.sgInvitado.id = :idViajero")
		    + " "
		    + " AND h.sgSolicitudEstancia.id = :idSolicitudEstancia "
		    + " AND h.hospedado = :hospedado "
		    + " AND h.cancelado = :cancelado "
		    + " AND h.eliminado = :eli ").setParameter("idViajero", (viajero.getUsuario() != null ? viajero.getUsuario().getId() : viajero.getSgInvitado().getId())).setParameter("idSolicitudEstancia", viajero.getSgSolicitudEstancia().getId()).
		    setParameter("hospedado", Constantes.BOOLEAN_TRUE).
		    setParameter("cancelado", Constantes.BOOLEAN_FALSE).
		    setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
	    if (re.size() > 0) {
		UtilLog4j.log.info(this, "Existió en la relacion huesped Hotel");
		return re.get(0);
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en findHuespedHotelTOViajero");
	    return null;
	}
    }

    
    public List<SgHuespedHotelVo> findAllSgHuespedHotelByNumeroReservacion(String numberoReservacion) {
	UtilLog4j.log.debug(this, "SgHuespedHotelImpl.findAllSgHuespedHotelByNumeroReservacion()");

	clearQuery();

	appendQuery("SELECT huho.ID, "); //0
	appendQuery("CASE WHEN dse.USUARIO IS NOT NULL THEN (SELECT nombre FROM USUARIO u WHERE u.ID=dse.USUARIO) "); //1
	appendQuery("WHEN dse.USUARIO IS NULL THEN (SELECT nombre FROM SG_INVITADO i WHERE i.ID=dse.SG_INVITADO) ");
	appendQuery("END AS HUESPED, ");
	appendQuery("huho.NUMERO_HABITACION AS NUM_RESERVACION, "); //2
	appendQuery("huho.FECHA_INGRESO, "); //3
	appendQuery("huho.FECHA_SALIDA, "); //4
	appendQuery("huho.SG_HOTEL_HABITACION, "); //5
	appendQuery("(SELECT h.ID FROM SG_HOTEL h WHERE h.ID=(SELECT hh.SG_HOTEL FROM SG_HOTEL_HABITACION hh WHERE hh.ID=huho.SG_HOTEL_HABITACION)) AS ID_SG_HOTEL "); //6
	appendQuery("FROM SG_HUESPED_HOTEL huho, SG_DETALLE_SOLICITUD_ESTANCIA dse ");
	appendQuery("WHERE huho.NUMERO_HABITACION='").append(numberoReservacion).append("' ");
	appendQuery("AND huho.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
	appendQuery("AND huho.HOSPEDADO='").append(Constantes.BOOLEAN_TRUE).append("' ");
	appendQuery("AND huho.SG_DETALLE_SOLICITUD_ESTANCIA=dse.ID;");

	UtilLog4j.log.debug(this, new StringBuilder().append("query: ").append(getStringQuery()).toString());

	List<Object[]> result = em.createNativeQuery(getStringQuery()).getResultList();
	List<SgHuespedHotelVo> list = new ArrayList<SgHuespedHotelVo>();
	SgHuespedHotelVo vo;

	for (Object[] objects : result) {
	    vo = new SgHuespedHotelVo();
	    vo.setId((Integer) objects[0]);
	    vo.setNombreHuesped((String) objects[1]);
	    vo.setReservacion((String) objects[2]);
	    vo.setFechaIngreso((Date) objects[3]);
	    vo.setFechaSalida((Date) objects[4]);
	    vo.setIdSgHotelHabitacion((Integer) objects[5]);
	    vo.setIdSgHotel((Integer) objects[6]);
	    list.add(vo);
	}

	UtilLog4j.log.debug(this, new StringBuilder().append("Se encontraron ").append(list != null ? list.size() : 0).append(" SgHuespedHotel").toString());

	return (list != null ? list : Collections.EMPTY_LIST);
    }

//    
//    public List<HuespedVo> findAllSgHuespedHotelLeaveTomorrowAndSgSolicitudEstanciaIsForViaje(int idSgOficina) {
//        UtilLog4j.log.debug(this, "SgHuespedHotelImpl.findAllSgHuespedHotelLeaveTomorrowAndSgSolicitudEstanciaIsForViaje");
//
//        Date d = this.siManejoFechaLocal.fechaSumarDias(new Date(), 1);
//
//        clearQuery();
//
//        appendQuery("select hh.ID, "); //0
//        appendQuery("case ");
//        appendQuery("when dse.SG_TIPO_ESPECIFICO=19 then (select u.NOMBRE from usuario u where u.ELIMINADO='False' and u.ID=dse.USUARIO) ");
//        appendQuery("when dse.SG_TIPO_ESPECIFICO=20 then (select i.NOMBRE from SG_INVITADO i where i.ELIMINADO='False' and i.ID=dse.SG_INVITADO) ");
//        appendQuery("end as nombre_huesped, "); //1
//        appendQuery("case ");
//        appendQuery("when dse.SG_TIPO_ESPECIFICO=19 then (select u.EMAIL from usuario u where u.ELIMINADO='False' and u.ID=dse.USUARIO) ");
//        appendQuery("when dse.SG_TIPO_ESPECIFICO=20 then '' ");
//        appendQuery("end as email_huesped, "); //2
//        appendQuery("hh.FECHA_INGRESO, "); //3
//        appendQuery("hh.FECHA_SALIDA, "); //4
//        appendQuery("hh.SG_SOLICITUD_ESTANCIA as id_sg_solicitud_estancia, "); //5
//        appendQuery("se.CODIGO as codigo_sg_solicitud_estancia, "); //6
//        appendQuery("hh.SG_DETALLE_SOLICITUD_ESTANCIA as id_sg_detalle_solicitud_estancia, "); //7
//        appendQuery("o.ID as id_sg_oficina, "); //8
//        appendQuery("o.NOMBRE as nombre_sg_oficina, "); //9
//        appendQuery("hoha.SG_HOTEL as id_sg_hotel, "); //10
//        appendQuery("h.PROVEEDOR as id_proveedor, "); //11
//        appendQuery("p.NOMBRE as nombre_proveedor, "); //12
//        appendQuery("hh.NUMERO_HABITACION as numero_reservacion, "); //13
//        appendQuery("dse.SG_TIPO_ESPECIFICO as id_sg_tipo_especifico, "); //14
//        appendQuery("g.ID as id_gerencia, "); //15
//        appendQuery("g.NOMBRE as nombre_gerencia "); //16
//        appendQuery("from SG_HUESPED_HOTEL hh, SG_HOTEL_HABITACION hoha, SG_SOLICITUD_ESTANCIA se, SG_HOTEL h, SG_DETALLE_SOLICITUD_ESTANCIA dse, SG_OFICINA o, GERENCIA g, PROVEEDOR p ");
//        appendQuery("where hh.HOSPEDADO='").append(Constantes.BOOLEAN_TRUE).append("' ");
//        appendQuery("and hh.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and hh.CANCELADO='").append(Constantes.BOOLEAN_FALSE).append("' ");
//        appendQuery("and hh.FECHA_SALIDA=cast('").append(Constantes.FMT_yyyyMMdd.format(d)).append("' as date) ");
//
//        appendQuery("and((select count(*) ");
//        appendQuery("from SG_HP_HOTEL_SI_MOVIMIENTO hmov, SI_MOVIMIENTO mov, SI_OPERACION o ");
//        appendQuery("where hmov.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and hmov.SG_HUESPED_HOTEL=hh.ID ");
//        appendQuery("and mov.ID=hmov.SI_MOVIMIENTO ");
//        appendQuery("and mov.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and o.ID=mov.SI_OPERACION ");
//        appendQuery("and o.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and o.ID=9)=0 ");
//        appendQuery("or (select hmov.FECHA_GENERO ");
//        appendQuery("from SG_HP_HOTEL_SI_MOVIMIENTO hmov, SI_MOVIMIENTO mov, SI_OPERACION o ");
//        appendQuery("where hmov.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and hmov.SG_HUESPED_HOTEL=hh.ID ");
//        appendQuery("and mov.ID=hmov.SI_MOVIMIENTO ");
//        appendQuery("and mov.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and o.ID=mov.SI_OPERACION ");
//        appendQuery("and o.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and o.ID=9) != cast('NOW' as date)) ");
//
//        appendQuery("and hoha.ID=hh.SG_HOTEL_HABITACION ");
//        appendQuery("and hoha.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and h.ID=hoha.SG_HOTEL ");
//        appendQuery("and h.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and p.ID=h.PROVEEDOR ");
//        appendQuery("and p.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and se.ID=hh.SG_SOLICITUD_ESTANCIA ");
//        appendQuery("and g.ID=se.GERENCIA ");
//        appendQuery("and g.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and dse.ID=hh.SG_DETALLE_SOLICITUD_ESTANCIA ");
//        appendQuery("and dse.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and o.ID=se.SG_OFICINA ");
//        appendQuery("and o.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//
//        appendQuery("and hh.SG_SOLICITUD_ESTANCIA in( ");
//        appendQuery("select se.ID ");
//        appendQuery("from SG_SOLICITUD_ESTANCIA se ");
//        appendQuery("where se.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and se.SG_OFICINA=").append(idSgOficina).append(" ");
//        appendQuery("and se.ESTATUS <> ").append(Constantes.ESTATUS_SOLICITUD_ESTANCIA_CANCELADA).append(" ");
//        appendQuery("and se.ID not in ( ");
//        appendQuery("select mov.SG_SOLICITUD_ESTANCIA from SG_SOL_EST_SI_MOVIMIENTO mov where mov.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("') ");
//        appendQuery("and se.id in ( ");
//        appendQuery("select distinct(vi.SG_SOLICITUD_ESTANCIA ) ");
//        appendQuery("from sg_viajero vi, SG_SOLICITUD_VIAJE sv, SG_TIPO_SOLICITUD_VIAJE tsv ");
//        appendQuery("where vi.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and sv.ID=vi.SG_SOLICITUD_VIAJE ");
//        appendQuery("and sv.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and sv.ESTATUS <> ").append(Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO).append(" ");
//        appendQuery("and sv.OFICINA_DESTINO is not null ");
//        appendQuery("and tsv.ID=sv.SG_TIPO_SOLICITUD_VIAJE ");
//        appendQuery("and tsv.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
//        appendQuery("and tsv.SG_TIPO_ESPECIFICO=").append(Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE).append(" ");
//        appendQuery("and vi.SG_SOLICITUD_ESTANCIA is not null)) ");
//
//        UtilLog4j.log.debug(this, new StringBuilder().append("query: ").append(getStringQuery()).toString());
//
//        HuespedVo vo;
//        List<Object[]> result = em.createNativeQuery(getStringQuery()).getResultList();
//        List<HuespedVo> list = new ArrayList<HuespedVo>();
//
//        for (Object[] objects : result) {
//            vo = new HuespedVo();
//            vo.setId((Integer) objects[0]);
//            vo.setNombreHuesped((String) objects[1]);
//            vo.setEmailHuesped((String) objects[2]);
//            vo.setFechaIngreso(((Date) objects[3]));
//            vo.setFechaSalida((Date) objects[4]);
//            vo.setIdSgSolicitudEstancia((Integer) objects[5]);
//            vo.setCodigoSgSolicitudEstancia((String) objects[6]);
//            vo.setIdSgDetalleSolicitudEstancia((Integer) objects[7]);
//            vo.setIdSgOficina((Integer) objects[8]);
//            vo.setNombreSgOficina((String) objects[9]);
//            vo.setIdSgHotel((Integer) objects[10]);
//            vo.setIdProveedor((Integer) objects[11]);
//            vo.setNombreProveedorHotel((String) objects[12]);
//            vo.setReservacion((String) objects[13]);
//            vo.setIdSgTipoEspecifico((Integer) objects[14]);
//            vo.setIdGerencia((Integer) objects[15]);
//            vo.setNombreGerencia((String) objects[16]);
//            vo.setHuespedStaff(false);
//            list.add(vo);
//        }
//
//        UtilLog4j.log.debug(this, new StringBuilder().append("Se encontraron ").append(list.isEmpty() ? "0" : list.size()).append(" Huésped").toString());
//
//        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
//    }
    
    public List<HuespedVo> traerHuespedesHotelConSalidaHoyPorSolicitud(int idSolicitudEstancia) {
	List<HuespedVo> list = null;
	try {
	    clearQuery();
	    appendQuery(" Select hhot.id,");//0
	    appendQuery(" hhot.SG_SOLICITUD_ESTANCIA,");//1
	    appendQuery(" hhot.FECHA_GENERO,");//2
	    appendQuery(" hhot.FECHA_INGRESO,");//3
	    appendQuery(" hhot.FECHA_REAL_INGRESO,");//4
	    appendQuery(" hhot.FECHA_SALIDA,");//5
	    appendQuery(" hhot.FECHA_REAL_SALIDA,");//6
	    appendQuery(" hhot.NUMERO_HABITACION,");//7
	    appendQuery(" hhot.SG_DETALLE_SOLICITUD_ESTANCIA,");//8
	    appendQuery(" hhot.SG_HOTEL_HABITACION,");//9
	    appendQuery(" huesped.USUARIO,");//10

	    appendQuery(" case when huesped.sg_invitado is not null then");
	    appendQuery(" huesped.sg_invitado ");
	    appendQuery(" else 0 end, "); //11

	    appendQuery(" case when huesped.USUARIO is not null then ");
	    appendQuery(" (select u.nombre From usuario u where u.id = huesped.USUARIO)");
	    appendQuery(" else (select i.nombre From usuario i where i.id = huesped.sg_invitado)");
	    appendQuery(" end as usuario,");//12

	    appendQuery(" case when huesped.USUARIO is not null then ");
	    appendQuery(" 'true'");
	    appendQuery(" else 'false'");
	    appendQuery(" end as isUsuario,");//13

	    appendQuery(" case when huesped.USUARIO is not null then ");
	    appendQuery(" (select u.EMAIL From usuario u where u.id = huesped.USUARIO)");
	    appendQuery(" else ''");
	    appendQuery(" end as correo,");//14
	    appendQuery(" pro.nombre as hotel,");//15
	    appendQuery(" tipoEsp.NOMBRE as tipo_habitacion,");//16
	    appendQuery(" tipoHuesped.NOMBRE,");//17
	    appendQuery(" solEst.CODIGO,");//18
	    appendQuery(" solEst.GERENCIA");//19

	    appendQuery(" From SG_HUESPED_HOTEL hhot,");
	    appendQuery(" SG_DETALLE_SOLICITUD_ESTANCIA huesped,");
	    appendQuery(" SG_HOTEL_HABITACION hab,");
	    appendQuery(" SG_TIPO_ESPECIFICO tipoEsp,");
	    appendQuery(" SG_TIPO_ESPECIFICO tipoHuesped,");
	    appendQuery(" sg_hotel ho,");
	    appendQuery(" proveedor pro, ");
	    appendQuery(" SG_SOLICITUD_ESTANCIA solEst");

	    appendQuery(" where hhot.HOSPEDADO = 'True'");
	    appendQuery(" and hhot.CANCELADO = 'False'");
	    appendQuery(" and hhot.ELIMINADO = 'False'");
	    appendQuery(" and hhot.SG_DETALLE_SOLICITUD_ESTANCIA = huesped.ID");
	    appendQuery(" and hhot.SG_HOTEL_HABITACION = hab.id ");
	    appendQuery(" and hab.SG_TIPO_ESPECIFICO = tipoEsp.id");
	    appendQuery(" and tipoHuesped.id = huesped.SG_TIPO_ESPECIFICO");
	    appendQuery(" and hab.SG_HOTEL = ho.id");
	    appendQuery(" and ho.PROVEEDOR = pro.id    ");
	    appendQuery(" and hhot.SG_SOLICITUD_ESTANCIA = solEst.id ");
	    appendQuery(" and hhot.SG_SOLICITUD_ESTANCIA  = ");
	    appendQuery(idSolicitudEstancia);
	    appendQuery(" and (hhot.FECHA_SALIDA = cast('now' as Date))");
	    //or hhot.FECHA_SALIDA = cast('now' as Date)
	    UtilLog4j.log.info(this, "++++++++++++++ " + getStringQuery());
	    List<Object[]> lista = em.createNativeQuery(getStringQuery()).getResultList();
	    if (lista != null && !lista.isEmpty()) {
		list = new ArrayList<HuespedVo>();
		for (Object[] objects : lista) {
		    list.add(castHuespedVo(objects));
		}
	    }

	    return list;

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exception al obtener los huespedes en hotel " + e.getMessage());
	    return null;
	}
    }

    private HuespedVo castHuespedVo(Object[] objects) {
	HuespedVo vo = new HuespedVo();
	vo.setId((Integer) objects[0]);
	vo.setIdSgSolicitudEstancia((Integer) objects[1]);
	vo.setFechaGenero((Date) objects[2]);
	vo.setFechaIngreso(((Date) objects[3]));
	vo.setFechaSalida((Date) objects[5]);
	vo.setNumeroSgStaffHabitacion((String) objects[7]);
	vo.setIdSgDetalleSolicitudEstancia((Integer) objects[8]);
	vo.setIdSgHotelHabitacion((Integer) objects[9]);
	vo.setIdUsuario((String) objects[10]);
	vo.setIdInvitado((Integer) objects[11]);
	vo.setNombreHuesped((String) objects[12]);//nombre del usuario o el invitado
	vo.setInvitado(Boolean.parseBoolean((String) objects[13]));
	vo.setEmailHuesped((String) objects[14]); //Correo del usuario (No invitado)
	vo.setNombreProveedorHotel((String) objects[15]);
	vo.setNombreHabitacion((String) objects[16]);
	vo.setTipohHuesped((String) objects[17]);
	//18 y //19 no van
	return vo;
    }

    private void escribirLog(String mensaje) {
	UtilLog4j.log.debug(this, mensaje);
    }

    
    public List<SgHuespedHotelVo> traerHuespedPorHotelPorFechaSalidaHoy(int idHotel) {
	UtilLog4j.log.info(this, "traerHuespedPorHotelPorFechaSalidaHoy" + idHotel);
	List<SgHuespedHotelVo> list = null;
	try {
	    clearQuery();
	    appendQuery(" select h.ID,");//0
	    appendQuery(" h.SG_DETALLE_SOLICITUD_ESTANCIA,");//1
	    appendQuery(" h.SG_HOTEL_HABITACION,");//2
	    appendQuery(" h.SG_SOLICITUD_ESTANCIA,");//3
	    //appendQuery(" h.NUMERO_HABITACION,");
	    //appendQuery(" h.SG_TIPO,");
	    appendQuery(" h.SG_TIPO_ESPECIFICO,");//4
	    appendQuery(" h.FECHA_INGRESO,");//5
	    appendQuery(" h.FECHA_SALIDA");//6
	    appendQuery(" From SG_HUESPED_HOTEL h,SG_HOTEL_HABITACION hh");
	    appendQuery(" Where h.HOSPEDADO = 'True'");
	    appendQuery(" and hh.SG_HOTEL = ");
	    appendQuery(idHotel);
	    appendQuery(" and h.ELIMINADO='False' ");
	    appendQuery(" and h.CANCELADO='False' ");
	    appendQuery(" and (h.FECHA_SALIDA = cast('now' as Date))");
	    appendQuery(" and h.SG_HOTEL_HABITACION = hh.ID");

	    UtilLog4j.log.info(this, "QUERY >>> " + getStringQuery());
	    List<Object[]> lista = em.createNativeQuery(getStringQuery()).getResultList();

	    if (lista != null && !lista.isEmpty()) {
		list = new ArrayList<SgHuespedHotelVo>();
		for (Object[] o : lista) {
		    SgHuespedHotelVo vo = new SgHuespedHotelVo();
		    vo.setId((Integer) o[0]);
		    vo.setIdSgDetalleSolicitudEstancia((Integer) o[1]);
		    vo.setIdSgHotelHabitacion((Integer) o[2]);
		    vo.setIdSgSolicitudEstancia((Integer) o[3]);
		    vo.setIdSgTipoEspecifico((Integer) o[4]);
		    vo.setFechaIngreso((Date) o[5]);
		    vo.setFechaSalida((Date) o[6]);
		    list.add(vo);
		}
	    }
	    return list;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traerHuespedPorHotelFechaSaludaHoy " + e.getMessage());
	    return null;
	}
    }

    
    public List<Integer> traerHuespedPorHotelPorFechaSalidaHoy() {
	escribirLog("traerHuespedPorHotelPorFechaSalidaHoy");
	List<Integer> lista = new ArrayList<>();
	try {
	    clearQuery();

	    appendQuery(" SELECT hho.ID ");
	    appendQuery(" from Sg_Oficina o ");
	    appendQuery(" inner join Sg_Hotel h on h.SG_OFICINA = o.ID and h.ELIMINADO = 'False' ");
	    appendQuery(" inner join PROVEEDOR p on p.ID = h.PROVEEDOR and p.ELIMINADO = 'False' ");
	    appendQuery(" inner join SG_HOTEL_HABITACION hab on hab.SG_HOTEL = h.ID and hab.ELIMINADO = 'False' ");
	    appendQuery(" inner join SG_HUESPED_HOTEL hho on hho.SG_HOTEL_HABITACION = hab.ID  ");
	    appendQuery(" 				and hho.HOSPEDADO = 'True'  ");
	    appendQuery(" 				and hho.ELIMINADO= 'False'  ");
	    appendQuery(" 				and hho.CANCELADO='False'  ");
	    appendQuery(" 				and hho.FECHA_SALIDA <= cast('now' as Date) ");
	    appendQuery(" WHERE o.VISTO_BUENO = 'True' ");
	    appendQuery(" AND o.ELIMINADO = 'False' ");
	    appendQuery(" ORDER BY o.id ASC ");

	    lista = em.createNativeQuery(getStringQuery()).getResultList();
	    escribirLog("Lista de huespedes  " + (lista != null ? lista.size() : 0));

	} catch (Exception e) {
	    escribirLog("Excepcion al traerHuespedPorHotelFechaSaludaHoy " + e.getMessage());
	    lista = new ArrayList<>();
	}
	return lista;
    }

    
    public List<SgHuespedHotelVo> traerTotalHospedados(int idOficina, String inicio, String fin) {
	clearQuery();
	List<SgHuespedHotelVo> lhuesped = new ArrayList<>();;
	query.append("select  p.NOMBRE, count(hh.id) from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_SOLICITUD_ESTANCIA se on hh.SG_SOLICITUD_ESTANCIA = se.ID");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join PROVEEDOR p on hot.PROVEEDOR = p.ID");
	query.append("  where hh.FECHA_REAL_INGRESO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
	query.append("  and se.SG_OFICINA = ").append(idOficina);
	query.append("  and hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by p.NOMBRE");
	//
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    lhuesped.add(castTotalHuespedVo(objects));
	}
	return lhuesped;
    }

    private SgHuespedHotelVo castTotalHuespedVo(Object[] objects) {
	SgHuespedHotelVo hotelVo = new SgHuespedHotelVo();
	hotelVo.setHotel((String) objects[0]);
	hotelVo.setTotal((Long) objects[1]);
	return hotelVo;
    }

    
    public List<HuespedVo> traerHospedadosHotel(int idOficina, String hotel, String inicio, String fin) {
	clearQuery();
	List<HuespedVo> lhuesped = new ArrayList<HuespedVo>();
	query.append("select  u.NOMBRE, inv.NOMBRE,  p.NOMBRE, hh.FECHA_REAL_INGRESO, hh.FECHA_REAL_SALIDA, hh.NUMERO_HABITACION, adj.id, adj.uuid, g.nombre");
	query.append("  from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_SOLICITUD_ESTANCIA se on hh.SG_SOLICITUD_ESTANCIA = se.ID");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join PROVEEDOR p on hot.PROVEEDOR = p.ID");
	query.append("      inner join SG_DETALLE_SOLICITUD_ESTANCIA dse on hh.SG_DETALLE_SOLICITUD_ESTANCIA  = dse.ID");
	query.append("      left join USUARIO u on dse.USUARIO = u.ID");
	query.append("      left join SG_INVITADO inv on dse.SG_INVITADO = inv.ID");
	query.append("      left join SI_ADJUNTO adj on hh.SI_ADJUNTO = adj.ID");
	query.append("      inner join gerencia g on se.gerencia = g.id");
	query.append("  where hh.FECHA_REAL_INGRESO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
	query.append("  and se.SG_OFICINA = ").append(idOficina);
	query.append("  and p.nombre = '").append(hotel).append("'");
	query.append("  and hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  order by hh.FECHA_REAL_INGRESO asc");
	//
	List<Object[]> lobj = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lobj) {
	    lhuesped.add(castReporteHuespedVo(objects));
	}
	return lhuesped;
    }

    
    public List<HuespedVo> traerHospedadosHotel(String hotel, String mesAnio) {
	clearQuery();
        String[] cad = mesAnio.split("/");
	List<HuespedVo> lhuesped = new ArrayList<>();
	query.append("select  u.NOMBRE, inv.NOMBRE,  p.NOMBRE, hh.FECHA_REAL_INGRESO, hh.FECHA_REAL_SALIDA, ");
	query.append("  hh.NUMERO_HABITACION, adj.id, '").append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W='||hh.SI_ADJUNTO || '&ZWZ3W=' || adj.UUID, g.nombre");
	query.append("  from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_SOLICITUD_ESTANCIA se on hh.SG_SOLICITUD_ESTANCIA = se.ID");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join PROVEEDOR p on hot.PROVEEDOR = p.ID");
	query.append("      inner join SG_DETALLE_SOLICITUD_ESTANCIA dse on hh.SG_DETALLE_SOLICITUD_ESTANCIA  = dse.ID");
	query.append("      left join USUARIO u on dse.USUARIO = u.ID");
	query.append("      left join SG_INVITADO inv on dse.SG_INVITADO = inv.ID");
	query.append("      left join SI_ADJUNTO adj on hh.SI_ADJUNTO = adj.ID");
	query.append("      inner join gerencia g on se.gerencia = g.id");
	query.append("  where extract(month from hh.FECHA_REAL_INGRESO ) = ").append(cad[0]);
        query.append("  and extract(year from hh.FECHA_REAL_INGRESO ) = ").append(cad[1]);
	query.append("  and p.nombre = '").append(hotel).append("'");
	query.append("  and hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  order by hh.FECHA_REAL_INGRESO asc");
	//
	//
	List<Object[]> lobj = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lobj) {
	    lhuesped.add(castReporteHuespedVo(objects));
	}
	return lhuesped;
    }

    private HuespedVo castReporteHuespedVo(Object[] objects) {
	HuespedVo huespedVo = new HuespedVo();
	String huesped = (String) objects[0] != null ? (String) objects[0] : (String) objects[1];
	huespedVo.setNombreHuesped(huesped);
	huespedVo.setNombreProveedorHotel((String) objects[2]);
	huespedVo.setFechaIngreso((Date) objects[3]);
	huespedVo.setFechaSalida((Date) objects[4]);
	huespedVo.setNumeroHabitacionHotel((String) objects[5]);
	huespedVo.setIdAdjunto((Integer) objects[6]);
	huespedVo.setAdjuntoUUID((String) objects[7]);
	huespedVo.setNombreGerencia((String) objects[8]);
	return huespedVo;
    }

    
    public List<PagoServicioVo> buscarGastoHotelOficina(int idOficina, int anio) {
	clearQuery();
	List<PagoServicioVo> lhuesped = new ArrayList<>();;
	query.append("select  p.NOMBRE, sum(hab.PRECIO) from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_SOLICITUD_ESTANCIA se on hh.SG_SOLICITUD_ESTANCIA = se.ID");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join PROVEEDOR p on hot.PROVEEDOR = p.ID");
	query.append("  where se.SG_OFICINA = ").append(idOficina);
	if (anio > 0) {
	    query.append("	and extract(year from hh.FECHA_REAL_INGRESO) = ").append(anio);
	}
	query.append("  and hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by p.NOMBRE");
	//
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    lhuesped.add(castTotalGatosHotelVo(objects));
	}
	return lhuesped;
    }

    
    public List<PagoServicioVo> buscarGastoHotelOficinaPorAnio(int idOficina, int hotel, int anio) {
	clearQuery();
	List<PagoServicioVo> lhuesped = new ArrayList<>();
	query.append("select SUBSTRING(to_char(hh.FECHA_REAL_INGRESO, 'DD/MM/YYYY'),4,10), sum(hab.PRECIO) from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_SOLICITUD_ESTANCIA se on hh.SG_SOLICITUD_ESTANCIA = se.ID");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join PROVEEDOR p on hot.PROVEEDOR = p.ID");
	query.append("  where se.SG_OFICINA = ").append(idOficina);
	query.append("	and hot.proveedor = ").append(hotel);
	if (anio > 0) {
	    query.append("	and extract(year from hh.FECHA_REAL_INGRESO) = ").append(anio);
	}
	query.append("  and hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by SUBSTRING(to_char(hh.FECHA_REAL_INGRESO, 'DD/MM/YYYY'),4,10) ");
	//System.out.println("consulta gasto : : : : : " + query.toString());
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    lhuesped.add(castTotalGatosHotelVo(objects));
	}
	return lhuesped;
    }

    private PagoServicioVo castTotalGatosHotelVo(Object[] objects) {
	PagoServicioVo hotelVo = new PagoServicioVo();
	hotelVo.setTipoEspecifico((String) objects[0]);
	BigDecimal bd = (BigDecimal) objects[1];
	hotelVo.setTotal((Double) bd.doubleValue());
	return hotelVo;
    }

    
    public List<PagoServicioVo> totalGastoHotel(int anio) {
	clearQuery();
	List<PagoServicioVo> lhuesped = new ArrayList<>();;
	query.append("select  o.NOMBRE, sum(hab.PRECIO) from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join SG_OFICINA o on hot.SG_OFICINA = o.ID");
	query.append("  where hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	if (anio > 0) {
	    query.append("  and extract(year from hh.FECHA_REAL_INGRESO) = ").append(anio);
	}
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by o.NOMBRE");
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    PagoServicioVo hotelVo = new PagoServicioVo();
	    hotelVo.getOficinaVO().setNombre((String) objects[0]);
	    BigDecimal bd = (BigDecimal) objects[1];
	    hotelVo.setTotal((Double) bd.doubleValue());
	    lhuesped.add(hotelVo);
	}
	return lhuesped;
    }

    
    public List<PagoServicioVo> totalHospedadosHotel(int anio) {
	clearQuery();
	List<PagoServicioVo> lhuesped = new ArrayList<>();
	query.append("select  o.NOMBRE, count(hh.id) from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join SG_OFICINA o on hot.SG_OFICINA = o.ID");
	query.append("  where hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	if (anio > 0) {
	    query.append("  and extract(year from hh.FECHA_REAL_INGRESO) = ").append(anio);
	}
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by o.NOMBRE");
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    PagoServicioVo hotelVo = new PagoServicioVo();
	    hotelVo.getOficinaVO().setNombre((String) objects[0]);
	    hotelVo.setTotalEntero(objects[1] != null ? (Long) objects[1] : 0);
	    lhuesped.add(hotelVo);
	}
	return lhuesped;
    }

    
    public List<PagoServicioVo> totalHospedadosHotelOficina(int oficina, int anio) {
	clearQuery();
	List<PagoServicioVo> lhuesped = new ArrayList<>();
	query.append("select  SUBSTRING(to_char(hh.fecha_real_ingreso, 'DD/MM/YYYY'),  4 ,10), count(hh.id) from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join SG_OFICINA o on hot.SG_OFICINA = o.ID");
	query.append("  where hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	query.append("  and o.id = ").append(oficina);
	if (anio > 0) {
	    query.append("  and extract(year from hh.FECHA_REAL_INGRESO) = ").append(anio);
	}
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by SUBSTRING(to_char(hh.fecha_real_ingreso, 'DD/MM/YYYY'),  4 ,10)");
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    PagoServicioVo hotelVo = new PagoServicioVo();
	    hotelVo.setTipoEspecifico((String) objects[0]);
	    hotelVo.setTotalEntero(objects[1] != null ? (Long) objects[1] : 0);
	    lhuesped.add(hotelVo);
	}
	return lhuesped;
    }

    
    public List<PagoServicioVo> totalHospedadosHotelOficinaMes(int oficina, int anio, int mes) {
	clearQuery();
	List<PagoServicioVo> lhuesped = new ArrayList<>();
	query.append("select  p.nombre, count(hh.id) from SG_HUESPED_HOTEL hh ");
	query.append("      inner join SG_HOTEL_HABITACION hab on hh.SG_HOTEL_HABITACION = hab.ID");
	query.append("      inner join SG_HOTEL hot on hab.SG_HOTEL = hot.ID");
	query.append("      inner join PROVEEDOR p on hot.PROVEEDOR = p.ID");
	query.append("  where hh.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("   and hot.sg_oficina = ").append(oficina);
	if (anio > 0) {
	    query.append("  and extract(year from hh.FECHA_REAL_INGRESO) = ").append(anio);
	}
	query.append("  and extract(month from hh.FECHA_REAL_INGRESO) = ").append(mes);
	query.append("  and hh.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by p.nombre");
	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	for (Object[] objects : lo) {
	    PagoServicioVo hotelVo = new PagoServicioVo();
	    hotelVo.setTipoEspecifico((String) objects[0]);
	    hotelVo.setTotalEntero(objects[1] != null ? (Long) objects[1] : 0);
	    lhuesped.add(hotelVo);
	}
	return lhuesped;
    }
}
