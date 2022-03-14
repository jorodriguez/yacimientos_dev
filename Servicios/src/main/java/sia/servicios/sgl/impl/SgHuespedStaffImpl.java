/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgDetalleSolicitudEstancia;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SgOficina;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgViajero;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.HuespedVo;
import sia.modelo.sgl.estancia.vo.SgHuespedStaffVo;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.hotel.vo.SgHotelTipoEspecificoVo;
import sia.modelo.sgl.vo.SgHuespedVO;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgHuespedStaffImpl extends AbstractFacade<SgHuespedStaff> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    private SgStaffHabitacionImpl sgStaffHabitacionRemote;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;
    @Inject
    private SgHuespedStaffSiMovimientoImpl sgHuespedStaffSiMovimientoRemote;
    @Inject
    private SgHuespedHotelServicioImpl sgHuespedHotelServicioRemote;
    @Inject
    private SgHotelTipoEspecificoImpl sgHotelTipoEspecificoRemote;
    private Date manana;    

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgHuespedStaffImpl() {
        super(SgHuespedStaff.class);
    }

    
    public void prolongarEstancia(int idSgHuespedStaff, Date nuevaFechaSalida, String idUsuario) {
        log("prolongarEstancia para huesped en staff");
        SgHuespedStaff sgHuespedStaffOriginal = find(idSgHuespedStaff);
        sgHuespedStaffOriginal.setFechaSalida(nuevaFechaSalida);
        sgHuespedStaffOriginal.setModifico(new Usuario(idUsuario));
        sgHuespedStaffOriginal.setFechaModifico(new Date());
        sgHuespedStaffOriginal.setHoraModifico(new Date());
        sgHuespedStaffOriginal.setProlongada(Constantes.BOOLEAN_TRUE);

        edit(sgHuespedStaffOriginal);
        
        //Movimiento
        SiMovimiento siMovimiento = this.siMovimientoRemote.save(Constantes.MOTIVO_PROLONGACION_ESTANCIA, Constantes.ID_SI_OPERACION_PROLONGAR_ESTANCIA, idUsuario);

        if (siMovimiento != null) {
            this.sgHuespedStaffSiMovimientoRemote.save(sgHuespedStaffOriginal.getId().intValue(), siMovimiento.getId().intValue(), idUsuario);
        }
    }

    
    public void actualizarFechaSalida(int idHuespedStaff, Date fechaNueva, String idUsuario) {
        try {
            //enviar correo de actualizacin de fecha en staff
            SgHuespedStaff hs = find(idHuespedStaff);
            if (notificacionServiciosGeneralesRemote.enviarCorreoRegistroProlongadoPorSemaforoHuespedStaff(hs, idUsuario)) {
                hs.setFechaSalida(fechaNueva);
                hs.setModifico(new Usuario(idUsuario));
                hs.setFechaModifico(new Date());
                hs.setHoraModifico(new Date());
                hs.setProlongada(Constantes.BOOLEAN_TRUE);
                edit(hs);
            } else {
                log("No se pudo enviar el correo de prolongacion de estancia en staff");
            }
        } catch (Exception ex) {
            Logger.getLogger(SgHuespedStaffImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    public SgHuespedStaff saveHuespedStaff(SgSolicitudEstanciaVo solicitudEstancia, int idDetalleEstancia, int idInvitado, String invitado, String empleado, String tipoEspecifico, String correoEmpleado,
            SgTipo tipo, SgTipoEspecifico tipoHuesped, SgStaffHabitacion habitacion, Date fechaIngreso, Date fechaSalida, String idUsuario) throws SIAException, Exception {
        log("SgHuespedStaffImpl.saveHuespedStaff()");

        SgHuespedStaff huespedStaff = null;
        boolean v = false;
        try {
            if (solicitudEstancia != null && idDetalleEstancia != 0 && tipo != null && tipoHuesped != null && habitacion != null && idUsuario != null && !idUsuario.equals("")) {

                v = notificacionServiciosGeneralesRemote.enviarCorreoRegistroHuespedStaff(idInvitado, invitado, empleado, tipoEspecifico, correoEmpleado, solicitudEstancia, habitacion, tipoHuesped, fechaIngreso, fechaSalida);
                if (v) {
                    huespedStaff = new SgHuespedStaff();
                    huespedStaff.setSgSolicitudEstancia(sgSolicitudEstanciaRemote.find(solicitudEstancia.getId()));
                    huespedStaff.setSgDetalleSolicitudEstancia(sgDetalleSolicitudEstanciaRemote.find(idDetalleEstancia));
                    huespedStaff.setSgTipo(tipo);
                    huespedStaff.setSgTipoEspecifico(tipoHuesped);
                    huespedStaff.setSgStaffHabitacion(habitacion);
                    huespedStaff.setFechaIngreso(fechaIngreso);
                    huespedStaff.setFechaSalida(fechaSalida);
                    huespedStaff.setGenero(new Usuario(idUsuario));
                    huespedStaff.setFechaGenero(new Date());
                    huespedStaff.setHoraGenero(new Date());
                    huespedStaff.setHospedado(Constantes.HUESPED_HOSPEDADO);
                    huespedStaff.setCancelado(Constantes.BOOLEAN_FALSE);
                    huespedStaff.setEliminado(Constantes.NO_ELIMINADO);

                    create(huespedStaff);
                    
                    //Actualizar el campo 'usado' a True del tipoEspecifico
                    tipoHuesped.setFechaGenero(new Date());
                    tipoHuesped.setHoraGenero(new Date());
                    tipoHuesped.setUsado(Constantes.BOOLEAN_TRUE);
                    sgTipoEspecificoRemote.edit(tipoHuesped);

                    //Actualizar el campo 'registrado' del ítem o integrante de la Solicitud de Estancia (SgDetalleSolicitudEstancia)
                    SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = sgDetalleSolicitudEstanciaRemote.find(idDetalleEstancia);
                    sgDetalleSolicitudEstancia.setRegistrado(Constantes.BOOLEAN_TRUE);
                    sgDetalleSolicitudEstancia.setFechaGenero(new Date());
                    sgDetalleSolicitudEstancia.setHoraGenero(new Date());
                    sgDetalleSolicitudEstanciaRemote.edit(sgDetalleSolicitudEstancia);

                    //Marcar como ocupada la Habitación
                    habitacion.setOcupada(Constantes.BOOLEAN_TRUE);
                    sgStaffHabitacionRemote.edit(habitacion);

                    //Actualizar el Status de la Solicitud de Estancia si ya fueron registrados todos los Huéspedes
                    List<DetalleEstanciaVO> integrantesSolicitud = sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(solicitudEstancia.getId(), Constantes.NO_ELIMINADO);
                    int contSolicitudesPendientesProcesar = 0;

                    /*
		     * Una Solicitud de Estancia se pasa a Status.TERMINADO
		     * cuando ya todos los integrantes han sido procesados
		     * (registrados o cancelados). Un integrante hospedado o
		     * cancelado de una u otra manera ya ha sido procesado.
		     * Casos para sumar: Registrado - Cancelado True - True -
		     * (No existe el caso) True - False - Integrante Procesada
		     * False - True - Integrante Procesada False - False -
		     * Integrante Pendiente por Procesar
                     */
                    if (integrantesSolicitud != null) {
                        log("tamnio integrantes " + integrantesSolicitud.size());
                        for (DetalleEstanciaVO integrante : integrantesSolicitud) {
                            log("Reg integrantes " + integrante.isRegistrado());
                            log("Can integrantes " + integrante.isCancelado());
                            if (!integrante.isRegistrado() && !integrante.isCancelado()) {
                                contSolicitudesPendientesProcesar++;
                            }
                        }
                    }
                    if (contSolicitudesPendientesProcesar == 0) {
                        sgSolicitudEstanciaRemote.finalizaSolicitud(solicitudEstancia.getId(), idUsuario);
                    }
                }
            } else {
                log("Faltan parámetros para poder guardar el Huésped en el Staff");
                throw new SIAException("Faltan parámetros para poder guardar el Huésped en el Staff");
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Ocurrió un error al guardar el Huésped", e);
            throw new SIAException("Ocurrió un error al guardar el Huésped.");
        }

        return huespedStaff;
    }

    
    public SgHuespedStaff update(SgHuespedStaff huespedStaff, String idUsuario) throws SIAException, Exception {
        edit(huespedStaff);        
        return huespedStaff;
    }

    
    public List<SgHuespedStaff> getAllHuespedesByStaffList(SgStaff staff, boolean hospedado, boolean cancelado) throws SIAException, Exception {
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.getAllHuespedesByStaffList()");

        List<SgHuespedStaff> huespedes = null;

        if (staff != null) {
            huespedes = em.createQuery("SELECT h FROM SgHuespedStaff h WHERE h.eliminado = :eliminado AND h.sgStaffHabitacion.SgStaff.id = :idStaff AND h.hospedado = :hospedado AND h.cancelado = :cancelado ORDER BY h.fechaIngreso ASC").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idStaff", staff.getId()).setParameter("hospedado", (hospedado ? Constantes.HUESPED_HOSPEDADO : Constantes.HUESPED_NO_HOSPEDADO)).setParameter("cancelado", (cancelado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE)).getResultList();
//            UtilLog4j.log.debug(this, new StringBuilder().append("Se encontraron ").append(huespedes.size()).append(" huéspedes en el Staff: ").append(staff.getId()).toString());
        } else {
            throw new SIAException(SgHuespedStaffImpl.class.getName(), "getAllHuespedesByStaffList()",
                    "Faltan datos para poder realizar la búsqueda de huéspedes por Staff",
                    ("Staff:" + (staff != null ? staff.getId() : null) + ", hospedado: " + hospedado));
        }
        return huespedes;
    }

    
    public List<SgHuespedStaff> getAllHuespedesByOficinaList(int oficina, boolean hospedado, boolean cancelado, String idSesion) throws SIAException, Exception {
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.getAllHuespedesByOficinaList()");

        List<SgHuespedStaff> huespedes = null;
        //+ " and h.sgSolicitudEstancia.usuarioHospeda.id = :idHospeda"
        //.setParameter("idHospeda", idSesion)
        if (oficina > 0) {
            huespedes = em.createQuery("SELECT h FROM SgHuespedStaff h WHERE h.eliminado = :eliminado AND h.sgStaffHabitacion.sgStaff.sgOficina.id = :idOficina AND h.hospedado = :hospedado  "
                    + " AND h.cancelado = :cancelado "
                    + " ORDER BY h.sgStaffHabitacion.sgStaff.nombre, h.sgStaffHabitacion.nombre ASC").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idOficina", oficina).setParameter("hospedado", (hospedado ? Constantes.HUESPED_HOSPEDADO : Constantes.HUESPED_NO_HOSPEDADO)).setParameter("cancelado", (cancelado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE)).getResultList();
            log("Se encontraron " + huespedes.size() + " huéspedes en los Staff de la Oficina: " + oficina);
        } else {
            throw new SIAException(SgHuespedStaffImpl.class.getName(), "getAllHuespedesByOficinaList()",
                    "Faltan datos para poder realizar la búsqueda de huéspedes por Staff",
                    ("Oficina:" + (oficina)
                    + ", hospedado: " + hospedado
                    + ", cancelado: " + cancelado));
        }
        return huespedes;
    }

    
    public SgHuespedStaff exitHuespedStaff(SgHuespedVO registroHuesped, Date fechaRealEntrada, Date fechaRealSalida, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.exitHuespedStaff()");
        SgHuespedStaff sgHuespedStaffObj = find(registroHuesped.getId());
        return this.exitHuespedStaff(sgHuespedStaffObj, fechaRealEntrada, fechaRealSalida, idUsuario);
    }

    
    public SgHuespedStaff exitHuespedStaff(SgHuespedStaff registroHuesped, Date fechaRealEntrada, Date fechaRealSalida, String idUsuario) throws SIAException, Exception {        
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.exitHuespedStaff()");
        boolean v;

        Usuario u = usuarioRemote.find(idUsuario);
        
        if (registroHuesped != null && fechaRealEntrada != null && fechaRealSalida != null && idUsuario != null && !idUsuario.equals("")) {

            //Poner el registro del Huesped como 'hospedado'=false y asignar las fechas reales de entrada y salida
            registroHuesped.setHospedado(Constantes.BOOLEAN_FALSE);
            registroHuesped.setFechaRealIngreso(fechaRealEntrada);
            registroHuesped.setFechaRealSalida(fechaRealSalida);
            registroHuesped.setModifico(u);
            registroHuesped.setFechaModifico(new Date());
            registroHuesped.setHoraModifico(new Date());
            try {
                v = notificacionServiciosGeneralesRemote.enviaCorreoSalidaHusped(u, registroHuesped);
                if (v) {
                    //Poner la Habitación como 'disponible'
                    List<SgHuespedStaffVo> l = getAllSgHuespedStaffBySgStaffHabitacion(registroHuesped.getSgStaffHabitacion().getId());

                    if (l != null && !l.isEmpty() && l.size() == 1) { //Si la habitación está ocupada por solo una persona, desocuparla, si no dejarla ocupada aún
                        SgStaffHabitacion sgStaffHabitacion = registroHuesped.getSgStaffHabitacion();
                        sgStaffHabitacion.setOcupada(Constantes.BOOLEAN_FALSE);
                        sgStaffHabitacionRemote.edit(sgStaffHabitacion);
                    }

                    try {
                        //Persistir el registro de Huésped
                        edit(registroHuesped);  
                    } catch (Exception e) {
                        throw e;
                    }
                }
            } catch (Exception e) {
                UtilLog4j.log.error(this, e.getMessage(), e);
                throw e;
            }
        } else {
            throw new SIAException(SgHuespedStaffImpl.class.getName(), "exitHuespedStaff()",
                    "Faltan datos para poder dar de baja el registro del Huésped en el Staff",
                    ("registroHuesped:" + (registroHuesped != null ? registroHuesped.getId() : null)
                    + "fechaRealEntrada: " + fechaRealEntrada
                    + "fechaRealSalida: " + fechaRealSalida));
        }

        return registroHuesped;
    }

    
    public boolean guardarCambioHuespedStaff(Usuario usuario, SgHuespedStaff sgHuespedStaff, SgStaffHabitacion habitacion, int idStaff,
            SgTipo sgTipo, SgHuespedHotel huespedHotel) {
        log("SgHuespedStaffImpl.guardarCambioHuespedStaff()");

        SgTipoEspecifico sgTipoEspecifico = sgTipoEspecificoRemote.find(huespedHotel.getSgTipoEspecifico().getId());
        boolean v = false;

        try {
            sgHuespedStaff.setSgTipo(sgTipo);
            sgHuespedStaff.setSgTipoEspecifico(sgTipoEspecifico);
            sgHuespedStaff.setSgStaffHabitacion(habitacion);
            sgHuespedStaff.setSgSolicitudEstancia(sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgSolicitudEstancia());
            sgHuespedStaff.setSgDetalleSolicitudEstancia(sgHuespedStaff.getSgDetalleSolicitudEstancia());
            sgHuespedStaff.setGenero(usuario);
            sgHuespedStaff.setFechaGenero(new Date());
            sgHuespedStaff.setHoraGenero(new Date());
            sgHuespedStaff.setHospedado(Constantes.BOOLEAN_TRUE);
            sgHuespedStaff.setCancelado(Constantes.BOOLEAN_FALSE);
            sgHuespedStaff.setEliminado(Constantes.NO_ELIMINADO);

            //int idInvitado, String invitado, String empleado, String tipoEspecifico, String correoEmpleado
            v = notificacionServiciosGeneralesRemote.enviarCorreoRegistroHuespedStaff((sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getId() : 0),
                    (sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre() : ""),
                    (sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : ""),
                    sgHuespedStaff.getSgDetalleSolicitudEstancia().getSgTipoEspecifico().getNombre(),
                    (sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? sgHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getEmail() : ""),
                    sgSolicitudEstanciaRemote.buscarEstanciaPorId(sgHuespedStaff.getSgSolicitudEstancia().getId()),
                    habitacion,
                    sgTipoEspecifico,
                    sgHuespedStaff.getFechaIngreso(),
                    sgHuespedStaff.getFechaSalida());
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error al enviar correo de notificación de asignación de Huésped a habitación de Staff****", e);
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
        if (v) {
            try {
                //Guardar el Nuevo registro del Huésped en Staff
                create(sgHuespedStaff);

                //Cambiar el estatus a a la habitacion
                habitacion.setOcupada(Constantes.BOOLEAN_TRUE);
                habitacion.setGenero(usuario);
                habitacion.setFechaGenero(new Date());
                habitacion.setHoraGenero(new Date());
                this.sgStaffHabitacionRemote.edit(habitacion);

                //Guardar cambios en el Registro de Hotel
                huespedHotel.setModifico(usuario);
                huespedHotel.setFechaModifico(new Date());
                huespedHotel.setHoraModifico(new Date());
                huespedHotel.setFechaSalida(new Date());
                huespedHotel.setHospedado(Constantes.BOOLEAN_FALSE);
                huespedHotel.setFechaRealSalida(huespedHotel.getFechaRealSalida() == null ? (new Date()) : huespedHotel.getFechaRealSalida());
                huespedHotel.setFechaRealIngreso(huespedHotel.getFechaRealIngreso() == null ? (new Date()) : huespedHotel.getFechaRealIngreso());
                this.sgHuespedHotelRemote.edit(huespedHotel);
            } catch (Exception e) {
                UtilLog4j.log.error(this, "Error al guardar el nuevo registro del Huésped en Staff", e);
            }
        }
        return v;
    }

    
    public List<SgHuespedStaff> getAllHuespedesBySolicitud(int sgSolicitudEstancia) {
        try {
            return em.createQuery("SELECT hs FROM SgHuespedStaff hs WHERE hs.sgSolicitudEstancia.id = :idSol").setParameter("idSol", sgSolicitudEstancia).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public List<SgHuespedStaff> getAllHuespedesBySolicitudHospedado(SgSolicitudEstancia sgSolicitudEstancia) {
        try {
            return em.createQuery("SELECT hs FROM SgHuespedStaff hs WHERE hs.sgSolicitudEstancia.id = :idSol "
                    + " AND hs.eliminado = :eli "
                    + " AND hs.hospedado = :hos "
                    + " ORDER BY hs.id ASC").setParameter("idSol", sgSolicitudEstancia.getId()).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public List<SgHuespedStaff> getAllHuespedesBySolicitudCancelado(SgSolicitudEstancia sgSolicitudEstancia) {
        try {
            return em.createQuery("SELECT hs FROM SgHuespedStaff hs WHERE hs.sgSolicitudEstancia.id = :idSol"
                    + " AND hs.eliminado = :eli "
                    + " AND hs.cancelado = :can "
                    + "ORDER BY hs.id ASC").setParameter("idSol", sgSolicitudEstancia.getId()).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public List<SgHuespedStaff> getAllHuespedesBySolicitudEstanciaTerminada(SgSolicitudEstancia sgSolicitudEstancia) {
        try {
            return em.createQuery("SELECT hs FROM SgHuespedStaff hs WHERE hs.sgSolicitudEstancia.id = :idSol"
                    + " AND hs.eliminado = :eli "
                    + " AND hs.cancelado = :can "
                    + " AND hs.hospedado= :hos "
                    + " ORDER BY hs.id ASC").setParameter("idSol", sgSolicitudEstancia.getId()).setParameter("can", Constantes.BOOLEAN_FALSE).setParameter("hos", Constantes.BOOLEAN_FALSE).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public void changeHuespedStaffToHotel(SgHuespedStaff registroActualHuespedStaff, int idHotel, int idHabitacionHotel, int numeroHabitacion, Date fechaRealIngresoHabitacionStaff, Date fechaRealSalidaHabitacionStaff, Date fechaIngreso, Date fechaSalida, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.changeHuespedStaffToHotel()");
        log("SgHuespedStaffImpl.changeHuespedStaffToHotel()");

//        boolean v;
        SgHuespedHotel huespedHotel = new SgHuespedHotel();
        SgStaffHabitacion sgStaffHabitacion = null;

        if (registroActualHuespedStaff != null && idHotel > 0 && idHabitacionHotel > 0 && numeroHabitacion > 0
                && fechaRealIngresoHabitacionStaff != null
                && fechaIngreso != null && fechaSalida != null && idUsuario != null && !idUsuario.equals("")) {
//            try {
            huespedHotel.setNumeroHabitacion(String.valueOf(numeroHabitacion));
            huespedHotel.setFechaIngreso(fechaIngreso);
            huespedHotel.setFechaSalida(fechaSalida);

            registroActualHuespedStaff.setHospedado(Constantes.BOOLEAN_FALSE);
            registroActualHuespedStaff.setFechaRealIngreso(fechaRealIngresoHabitacionStaff);
            registroActualHuespedStaff.setFechaRealSalida(fechaRealSalidaHabitacionStaff);
            registroActualHuespedStaff.setFechaModifico(new Date());
            registroActualHuespedStaff.setHoraModifico(new Date());
            registroActualHuespedStaff.setModifico(new Usuario(idUsuario));

            //Poner la Habitación como 'disponible'
            List<SgHuespedStaffVo> l = getAllSgHuespedStaffBySgStaffHabitacion(registroActualHuespedStaff.getSgStaffHabitacion().getId().intValue());
            log("* * * * *2 ");
            if (l != null && !l.isEmpty() && l.size() == 1) { //Si la habitación está ocupada por solo una persona, desocuparla, si no dejarla ocupada aún
                sgStaffHabitacion = registroActualHuespedStaff.getSgStaffHabitacion();
                sgStaffHabitacion.setOcupada(Constantes.BOOLEAN_FALSE);
                sgStaffHabitacionRemote.edit(sgStaffHabitacion);
            }

            try {
                //Guardar el Huésped en el Hotel
                boolean saveHuespedHotelSuccesfull;
                saveHuespedHotelSuccesfull = sgHuespedHotelRemote.guardarHuespedHotel(idUsuario, huespedHotel,
                        registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getId(),
                        registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getId() : 0,
                        registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre() : "",
                        registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado() == null ? registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : "",
                        registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getSgTipoEspecifico().getNombre(),
                        registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? registroActualHuespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getEmail() : "",
                        idHotel,
                        idHabitacionHotel,
                        registroActualHuespedStaff.getSgSolicitudEstancia().getId(),
                        registroActualHuespedStaff.getSgTipo(),
                        registroActualHuespedStaff.getSgTipoEspecifico().getId(), "");

                //Guardar los servicios proveídos por el hotel
                List<SgHotelTipoEspecificoVo> list = this.sgHotelTipoEspecificoRemote.getAllSgHotelTipoEspecificoBySgHotelAndProvided(idHotel, true, "id", true, false);
                for (SgHotelTipoEspecificoVo vo : list) {
                    this.sgHuespedHotelServicioRemote.save(huespedHotel.getId().intValue(), vo.getIdSgTipoEspecifico(), false, idUsuario);
                }

                if (!saveHuespedHotelSuccesfull) {
                    throw new SIAException(SgHuespedStaffImpl.class.getName(),
                            "changeHuespedStaffToHotel",
                            "No se pudo guardar el Huésped en el Hotel. Porfavor contacta el equipo del SIA al correo soportesia@ihsa.mx",
                            "No se pudo guardar el Huésped al Hotel. Ver el método \"SgHuespedHotelRemote.guardarHuespedHotel\"");
                } else {
                    //Si no falló el registro del Huésped en el Hotel, persistir todos los registros anteriores
                    try {
                        edit(registroActualHuespedStaff);
                        if (sgStaffHabitacion != null) {
                            sgStaffHabitacionRemote.edit(sgStaffHabitacion);
                        }
                    } catch (Exception e) {
                        UtilLog4j.log.error(this, e.getMessage(), e);
                        throw e;
                    }
                }
            } catch (Exception e) {
                UtilLog4j.log.error(this, e.getMessage(), e);
                throw e;
            }
        } else {
            throw new SIAException(SgHuespedStaffImpl.class.getName(), "changeHuespedStaffToHotel()",
                    "Faltan datos para poder cambiar al Huésped de un Staff a un Hotel",
                    ("registroActualHuespedActual: " + (registroActualHuespedStaff != null ? registroActualHuespedStaff.getId() : null)
                    + " idHotel: " + idHotel
                    + " idHabitacionHotel: " + idHabitacionHotel
                    + " numeroHabitacion: " + numeroHabitacion
                    + " fechaRealIngresoHabitacionStaff: " + fechaRealIngresoHabitacionStaff
                    + " fechaIngreso: " + fechaIngreso
                    + " fechaSalida: " + fechaSalida));
        }
    }

    
    public void cancelHospedajeStaff(SgHuespedStaff registroHuespedStaff, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.cancelHospedajeStaff()");
        boolean v;

        //Marcar como No Hospedado y Cancelado
        if (registroHuespedStaff != null && idUsuario != null && !idUsuario.equals("")) {
            try {
                registroHuespedStaff.setCancelado(Constantes.BOOLEAN_TRUE);
                registroHuespedStaff.setHospedado(Constantes.HUESPED_NO_HOSPEDADO);
                registroHuespedStaff.setModifico(new Usuario(idUsuario));
                registroHuespedStaff.setFechaModifico(new Date());
                registroHuespedStaff.setHoraModifico(new Date());

                v = notificacionServiciosGeneralesRemote.enviaCorreoCancelaRegistroHuesped(usuarioRemote.find(idUsuario), registroHuespedStaff);

                if (v) {
                    //Marcar como No Hospedado y Cancelado al Integrante de la Solicitud
                    SgDetalleSolicitudEstancia integrante = registroHuespedStaff.getSgDetalleSolicitudEstancia();
                    integrante.setRegistrado(Constantes.BOOLEAN_FALSE);
                    integrante.setCancelado(Constantes.BOOLEAN_TRUE);

                    //Ṕoner la habitación como 'desocupada'
                    List<SgHuespedStaffVo> l = getAllSgHuespedStaffBySgStaffHabitacion(registroHuespedStaff.getSgStaffHabitacion().getId().intValue());

                    if (l != null && !l.isEmpty() && l.size() == 1) { //Si la habitación está ocupada por solo una persona, desocuparla, si no dejarla ocupada aún
                        SgStaffHabitacion sgStaffHabitacion = registroHuespedStaff.getSgStaffHabitacion();
                        sgStaffHabitacion.setOcupada(Constantes.BOOLEAN_FALSE);
                        sgStaffHabitacionRemote.edit(sgStaffHabitacion);
                    }

                    edit(registroHuespedStaff);
                    this.sgDetalleSolicitudEstanciaRemote.edit(integrante);

                    try {
                        //Actualizar el Status de la Solicitud de Estancia si ya fueron procesados (registrados o cancelados) todos los Huéspedes
                        SgSolicitudEstancia solicitudEstancia = registroHuespedStaff.getSgSolicitudEstancia();
                        List<DetalleEstanciaVO> integrantesSolicitud = sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(solicitudEstancia.getId(), Constantes.NO_ELIMINADO);
                        int contSolicitudesPendientesProcesar = 0;
                        /*
			 * Una Solicitud de Estancia se pasa a Status.TERMINADO
			 * cuando ya todos los integrantes han sido procesados
			 * (registrados o cancelados). Un integrante hospedado o
			 * cancelado de una u otra manera ya ha sido procesado.
			 * Casos para sumar: Registrado - Cancelado True - True
			 * - (No existe el caso) True - False - Integrante
			 * Procesada False - True - Integrante Procesada False -
			 * False - Integrante Pendiente por Procesar
                         */
                        for (DetalleEstanciaVO ise : integrantesSolicitud) {
                            if (!ise.isRegistrado() && !ise.isCancelado()) {
                                contSolicitudesPendientesProcesar++;
                            }
                        }
                        if (contSolicitudesPendientesProcesar == 0) {
                            sgSolicitudEstanciaRemote.finalizaSolicitud(solicitudEstancia.getId(), idUsuario);
                        }
                    } catch (Exception e) {
                        UtilLog4j.log.error(this, e.getMessage(), e);
                        throw e;
                    }
                }
            } catch (Exception e) {
                UtilLog4j.log.error(this, e.getMessage(), e);
                throw e;
            }
        } else {
            throw new SIAException(SgHuespedStaffImpl.class.getName(), "cancelHospedajeStaff()",
                    "Faltan datos para poder cambiar al Huésped de un Staff a un Hotel",
                    ("registroHuespedStaff: " + (registroHuespedStaff != null ? registroHuespedStaff.getId() : null)));
        }
    }

    
    public boolean cancelLougueStaffOfTraveler(SgViajero viajero, String motivoCancelaciom, Usuario usuario) {
        boolean ret = false;
        try {
            SgHuespedStaff huesped = findHuespedStafToViajero(viajero);
            if (huesped != null) {
                this.cancelHospedajeStaff(huesped, usuario.getId());

                // ENVIAR A SI_MOVIMIENTO
                //enviar a si movimiento
                SiMovimiento simo = this.siMovimientoRemote.guardarSiMovimiento(motivoCancelaciom, siOperacionRemote.find(3), usuario);
                if (simo != null) {
                    this.sgHuespedStaffSiMovimientoRemote.guardarHuespedStaffSiMovimiento(huesped, simo, usuario);
                }
                ret = true;
            }
            return ret;
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return false;
        }
    }

    private SgHuespedStaff findHuespedStafToViajero(SgViajero viajero) {
        List<SgHuespedStaff> list;
        try {
            list = em.createQuery("SELECT hs FROM SgHuespedStaff hs "
                    + " WHERE hs.sgSolicitudEstancia.id = :idSol"
                    + " AND " + (viajero.getUsuario() != null ? "hs.sgDetalleSolicitudEstancia.usuario.id = :idUsuario " : "hs.sgDetalleSolicitudEstancia.sgInvitado.id = :idUsuario")
                    + " AND hs.eliminado = :eli "
                    + " AND hs.cancelado = :can "
                    + " AND hs.hospedado= :hos ").setParameter("idSol", viajero.getSgSolicitudEstancia().getId()).setParameter("idUsuario", (viajero.getUsuario() != null ? viajero.getUsuario().getId() : viajero.getSgInvitado().getId())).setParameter("can", Constantes.BOOLEAN_FALSE).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
            if (list.size() > 0) {
                UtilLog4j.log.debug(this, "Esta en huesped staff..");
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Excepcion findHuespeStaffTo-Viajero", e);
            return null;
        }
    }

    
    public SgHuespedStaff changeHuespedStaff(SgHuespedStaff registroHuespedAnterior, SgStaffHabitacion nuevaHabitacion, Date fechaIngresoNuevaHabitacion, Date fechaSalidaNuevaHabitacion, Date fechaRealEntradaAntiguaHabitacion, Date fechaRealSalidaAntiguaHabitacion, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.changeHuespedStaff()");

        SgHuespedStaff nuevoRegistro = null;

        if (registroHuespedAnterior != null && nuevaHabitacion != null && fechaIngresoNuevaHabitacion != null && fechaSalidaNuevaHabitacion != null && fechaRealEntradaAntiguaHabitacion != null && fechaRealSalidaAntiguaHabitacion != null && idUsuario != null && !idUsuario.equals("")) {
            try {
                List<SgHuespedStaffVo> l = getAllSgHuespedStaffBySgStaffHabitacion(registroHuespedAnterior.getSgStaffHabitacion().getId().intValue());

                registroHuespedAnterior.setHospedado(Constantes.HUESPED_NO_HOSPEDADO);
                registroHuespedAnterior.setFechaRealIngreso(fechaRealEntradaAntiguaHabitacion);
                registroHuespedAnterior.setFechaRealSalida(fechaRealSalidaAntiguaHabitacion);
                registroHuespedAnterior.setModifico(new Usuario(idUsuario));
                registroHuespedAnterior.setFechaModifico(new Date());
                registroHuespedAnterior.setHoraModifico(new Date());

                edit(registroHuespedAnterior);
                try {
                    //"Desocupar" habitación anterior
                    if (l != null && !l.isEmpty() && l.size() == 1) { //Si la habitación está ocupada por solo una persona, desocuparla, si no dejarla ocupada aún
                        SgStaffHabitacion sgStaffHabitacion = registroHuespedAnterior.getSgStaffHabitacion();
                        sgStaffHabitacion.setOcupada(Constantes.BOOLEAN_FALSE);
                        sgStaffHabitacionRemote.edit(sgStaffHabitacion);
                    }

                    try {
//                        log("registroHuespedAnterior.getSgDetalleSolicitudEstancia");
//                        log("-1 " +sgSolicitudEstanciaRemote.buscarEstanciaPorId(registroHuespedAnterior.getSgSolicitudEstancia().getId()).getId());
//                        log("-2 "+registroHuespedAnterior.getSgDetalleSolicitudEstancia().getId());
//                        log("-3 "+(registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado().getId() : 0));
//                        log("-4 : " +(registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre() : ""));
//                        log("-5 "+(registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : ""));
//                        log("-6 "+(registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgTipoEspecifico().getNombre()));
//                        log("-7 "+(registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario().getEmail() : ""));
//                        log("--8 "+registroHuespedAnterior.getSgTipo());
//                        log("-9 "+registroHuespedAnterior.getSgTipoEspecifico());
//                        log("-10 "+nuevaHabitacion);
//                        log("-12 "+fechaIngresoNuevaHabitacion);
//                        log("-13 "+fechaSalidaNuevaHabitacion);
//                        log("-14 "+idUsuario);

                        nuevoRegistro = saveHuespedStaff(sgSolicitudEstanciaRemote.buscarEstanciaPorId(registroHuespedAnterior.getSgSolicitudEstancia().getId()),
                                registroHuespedAnterior.getSgDetalleSolicitudEstancia().getId(),
                                (registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado().getId() : 0),
                                (registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre() : ""),
                                (registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : ""),
                                (registroHuespedAnterior.getSgDetalleSolicitudEstancia().getSgTipoEspecifico().getNombre()),
                                (registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario() != null ? registroHuespedAnterior.getSgDetalleSolicitudEstancia().getUsuario().getEmail() : ""),
                                registroHuespedAnterior.getSgTipo(),
                                registroHuespedAnterior.getSgTipoEspecifico(),
                                nuevaHabitacion,
                                fechaIngresoNuevaHabitacion,
                                fechaSalidaNuevaHabitacion,
                                idUsuario);

                        try {
                            //Establecer como 'ocupada' la nueva Habitación
                            nuevaHabitacion.setOcupada(Constantes.BOOLEAN_TRUE);
//                            nuevaHabitacion.setFechaGenero(new Date());
//                            nuevaHabitacion.setHoraGenero(new Date());
//                            nuevaHabitacion.setGenero(usuarioService.find(idUsuario));
                            sgStaffHabitacionRemote.edit(nuevaHabitacion);
                        } catch (Exception e) {
                            UtilLog4j.log.error(this, e.getMessage(), e);
                            throw e;
                        }
                    } catch (SIAException siae) {
                        //throw siae;
                        log("exxepcion  " + siae.getMessage());
                    } catch (Exception e) {
                        //UtilLog4j.log.error(this, e.getMessage(), e);
                        log("exxepcion 2 " + e.getMessage());
                        throw e;
                    }
                } catch (Exception e) {
                    //UtilLog4j.log.error(this, e.getMessage(), e);
                    log("exxepcion  3 " + e.getMessage());
                    throw e;
                }
            } catch (Exception e) {
                //UtilLog4j.log.error(this, e.getMessage(), e);
                log("exxepcion  5 " + e.getMessage());
                throw e;
            }
        } else {
            throw new SIAException(SgHuespedStaffImpl.class.getName(), "changeHuespedStaff()",
                    "Faltan datos para poder cambiar al Huésped de Staff",
                    ("registroHuespedAnterior:" + (registroHuespedAnterior != null ? registroHuespedAnterior.getId() : null)
                    + "nuevaHabitacion: " + (nuevaHabitacion.getId() != null ? nuevaHabitacion : null)
                    + "fechaIngresoNuevaHabitacion: " + fechaIngresoNuevaHabitacion
                    + "fechaSalidaNuevaHabitacion" + fechaSalidaNuevaHabitacion
                    + "fechaRealEntradaAntiguaHabitacion" + fechaRealEntradaAntiguaHabitacion
                    + "fechaRealSalidaAntiguaHabitacion" + fechaRealSalidaAntiguaHabitacion));
        }
        return nuevoRegistro;
    }

    
    public List<SgHuespedVO> findAllVencimientoEstanciaPorStaff(String fechaVencimiento) {
        UtilLog4j.log.debug(this, "entrando a buscar vencimientos findAllVencimientoEstanciaPorStaff");
        List<SgHuespedVO> r = new ArrayList<SgHuespedVO>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select hs.ID, hs.FECHA_INGRESO, hs.FECHA_SALIDA ");
            sb.append(" from Sg_Oficina o ");
            sb.append(" inner join Sg_Staff st on st.SG_OFICINA = o.ID and st.ELIMINADO = 'False' ");
            sb.append(" inner join SG_STAFF_HABITACION stH on stH.SG_STAFF = st.ID and stH.ELIMINADO = 'False' ");
            sb.append(" inner join SG_HUESPED_STAFF hs on hs.SG_STAFF_HABITACION = stH.ID  ");
            sb.append(" 		and hs.FECHA_SALIDA  <= cast('").append(fechaVencimiento).append("' as date) ");
            sb.append(" 		AND hs.HOSPEDADO = 'True'  ");
            sb.append(" 		AND hs.ELIMINADO = 'False' ");
            sb.append(" WHERE o.VISTO_BUENO = 'True' ");
            sb.append(" AND o.ELIMINADO = 'False' ");
            sb.append(" ORDER BY o.id, st.ID ASC ");

            log("Q: : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] objects : lo) {
                r.add(castSgHuespedVO(objects));
            }
        } catch (Exception e) {
            log("Error al buscar la orden por consecutivo: " + e.getMessage());
            r = new ArrayList<SgHuespedVO>();
        }
        return r;
    }

    private SgHuespedVO castSgHuespedVO(Object[] obj) {
        SgHuespedVO o = new SgHuespedVO();
        o.setId((Integer) obj[0]);
        o.setFecha_ingreso((Date) obj[1]);
        o.setFecha_salida((Date) obj[2]);
        return o;
    }

    
    public List<SgHuespedStaff> findAllVencimientoEstanciaPorStaff(Date fechaVencimiento, SgStaff staff, int tipoEstancia) {
        UtilLog4j.log.debug(this, "entrando a buscar vencimientos findAllVencimientoEstanciaPorStaff");
        List<SgHuespedStaff> lista = new ArrayList<SgHuespedStaff>();
        try {
            if (tipoEstancia != 0) {
                log("El tipo de estancia fue por Periodo");
                lista = em.createQuery("SELECT hs FROM SgHuespedStaff hs "
                        + " WHERE hs.fechaSalida = :fechaVencimiento AND "
                        + " hs.sgTipoEspecifico.id = :idTipoEspecifico AND "
                        + " hs.hospedado = :hospedado AND "
                        + " hs.eliminado = :eli AND "
                        + " hs.sgStaffHabitacion.sgStaff = :Staff "
                        + " ORDER BY hs.id ASC").setParameter("idTipoEspecifico", tipoEstancia) //el id de el tipo especifico Periodo de prueba
                        .setParameter("fechaVencimiento", fechaVencimiento).setParameter("hospedado", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.BOOLEAN_FALSE).setParameter("Staff", staff).getResultList();
            } else {
                log("El tipo de estancia fue por todos...");
                lista = em.createQuery("SELECT hs FROM SgHuespedStaff hs "
                        + " WHERE hs.fechaSalida = :fechaVencimiento AND "
                        + " hs.hospedado = :hospedado AND "
                        + " hs.eliminado = :eli AND "
                        + " hs.sgStaffHabitacion.sgStaff = :Staff "
                        + " ORDER BY hs.id ASC").setParameter("fechaVencimiento", fechaVencimiento).setParameter("hospedado", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.BOOLEAN_FALSE).setParameter("Staff", staff).getResultList();
            }

        } catch (Exception e) {
            UtilLog4j.log.error(this, "Excepción en la consulta de vencimiento de estancia de staff ", e);
            lista = new ArrayList<SgHuespedStaff>();
        }
        return lista;
    }

    //Métodos para Seguridad
    
    public TreeSet<Integer> totalHospedadosStaff() {
        TreeSet<Integer> ts = new TreeSet<Integer>();
        try {
            List<SgOficina> lh = em.createQuery("select s.sgStaffHabitacion.sgStaff.sgOficina FROM SgHuespedStaff s WHERE s.hospedado = :t "
                    + " AND  s.eliminado = :eli").setParameter("t", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
            log("LH: " + lh.size());
            for (SgOficina sgOficina : lh) {
                ts.add(sgOficina.getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Ocurrio una excepcion hospedados staff", e);
            return null;
        }
        return ts;
    }

    
    public TreeSet<Integer> totalSalidaStaff() {
        TreeSet<Integer> ts = new TreeSet<Integer>();
        manana = siManejoFechaLocal.fechaSumarDias(new Date(), 1);
        try {
            List<SgOficina> lh = em.createQuery("select s.sgStaffHabitacion.sgStaff.sgOficina FROM SgHuespedStaff s WHERE "
                    + " s.fechaSalida = :date"
                    + " AND  s.hospedado = :hos"
                    + " AND  s.eliminado = :eli").setParameter("date", manana).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
            for (SgOficina sgOficina : lh) {
                ts.add(sgOficina.getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Ocurrio una excepcion salida staff ", e);
            return null;
        }
        return ts;
    }

    
    public TreeSet<Integer> totalCanceladoStaff() {
        TreeSet<Integer> ts = new TreeSet<Integer>();
        try {
            List<SgOficina> lh = em.createQuery("select s.sgStaffHabitacion.sgStaff.sgOficina FROM SgHuespedStaff s WHERE "
                    + " s.eliminado = :eli"
                    + " AND  s.fechaModifico = :date"
                    + " AND  s.cancelado = :can").setParameter("date", new Date()).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
            log("LH cancelado staff: " + lh.size());
            for (SgOficina sgOficina : lh) {
                ts.add(sgOficina.getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Ocurrio una excepcion cancelado staff", e);
        }
        return ts;
    }

    
    public int totalHuespedadosStaffPorOficina(int oficina) {
        return ((Long) em.createQuery("select count(s) FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.sgOficina.id = :ofi "
                + " AND s.hospedado = :hospedado"
                + " AND s.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hospedado", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina).getSingleResult()).intValue();
    }

    
    public int totalSalidaStaffPorOficina(int oficina) {
        return ((Long) em.createQuery("select count(s) FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.sgOficina.id = :ofi "
                + " AND s.fechaSalida = :date"
                + " AND s.eliminado = :eli"
                + " AND s.hospedado = :hos").setParameter("date", siManejoFechaLocal.fechaSumarDias(new Date(), 1)).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina).getSingleResult()).intValue();
    }

    
    public int totalCanceladoStaffPorOficina(int oficina) {
        return ((Long) em.createQuery("select count(s) FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.sgOficina.id = :ofi "
                + " AND  s.eliminado = :eli"
                + " AND  s.fechaModifico = :date"
                + " AND  s.cancelado = :can").setParameter("date", new Date()).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("ofi", oficina).getSingleResult()).intValue();
    }
//////////////    
//////////////    public List<SeguridadVO> traerHospedadosStaff() {
//////////////        try {
//////////////            List<SeguridadVO> ls = em.createQuery("SELECT new sia.servicios.vo.SeguridadVO(s.sgStaffHabitacion.sgStaff.sgOficina.id,"
//////////////                    + "s.sgStaffHabitacion.sgStaff.sgOficina.sgDireccion.ciudad, "
//////////////                    + "count(s)) FROM SgHuespedStaff s"
//////////////                    + " WHERE s.sgStaffHabitacion.sgStaff.sgOficina.id IN (Select o.id From SgOficina o) "
//////////////                    + " Group by s.sgStaffHabitacion.sgStaff.sgOficina.id").getResultList();
//////////////            log("LS: " + ls.size());
//////////////            return ls;
//////////////        } catch (Exception e) {
//////////////            log("Ocurrio una excepcion: " + e.getStackTrace().toString() + " Mensaje " + e.getMessage() + " causa " + e.getCause().toString());
//////////////            return null;
//////////////        }
////////////////////}

    
    public List<SgHuespedStaff> getAllExitByOficinaList(SgOficina oficina) {
        try {
            return em.createQuery("select s FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.sgOficina.id = :ofi "
                    + " AND s.fechaSalida = :date"
                    + " AND s.eliminado = :eli"
                    + " AND s.hospedado = :hos").setParameter("date", new Date()).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("ofi", oficina.getId()).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public List<SgHuespedStaff> getAllCancelByOficinaList(SgOficina oficina) {
        try {
            return em.createQuery("select s FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.sgOficina.id = :ofi "
                    + " AND  s.eliminado = :eli"
                    + " AND  s.fechaModifico = :date"
                    + " AND  s.cancelado = :can").setParameter("date", new Date()).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("ofi", oficina.getId()).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }

    }

    
    public List<SgHuespedStaff> traerHospedadoPorStaff(String idStaff) {
        return em.createQuery("select s FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.nombre = :staff "
                + " AND s.hospedado = :hospedado"
                + " AND s.cancelado = :cancel"
                + " AND s.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hospedado", Constantes.BOOLEAN_TRUE).setParameter("cancel", Constantes.BOOLEAN_FALSE).setParameter("staff", idStaff).getResultList();
    }

    
    public List<SgHuespedStaff> traerSalidaPorStaff(String idStaff) {
        try {
            manana = siManejoFechaLocal.fechaSumarDias(new Date(), 1);
            return em.createQuery("select s FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.nombre = :staff "
                    + " AND s.fechaSalida = :date"
                    + " AND s.eliminado = :eli"
                    + " AND s.hospedado = :hos").setParameter("date", manana).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("hos", Constantes.BOOLEAN_TRUE).setParameter("staff", idStaff).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public List<SgHuespedStaff> traerCanceladoPorStaff(String idStaff) {
        return em.createQuery("select s FROM SgHuespedStaff s WHERE s.sgStaffHabitacion.sgStaff.nombre = :staff "
                + " AND  s.eliminado = :eli"
                + " AND  s.fechaModifico = :date"
                + " AND  s.cancelado = :can").setParameter("date", new Date()).setParameter("can", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("staff", idStaff).getResultList();
    }

    
    public List traerHospedadosNativo() {
        Query q = em.createNativeQuery("select d.ciudad, st.nombre, count(*) "
                + " from sg_huesped_staff hs, sg_solicitud_estancia se, sg_staff_habitacion sh, sg_staff st, sg_oficina o, sg_direccion d"
                + " where hs.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND hs.cancelado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND hs.hospedado = '" + Constantes.BOOLEAN_TRUE + "'"
                + " AND hs.sg_solicitud_estancia = se.id"
                + " AND hs.sg_staff_habitacion = sh.id "
                + " AND sh.sg_staff = st.id  "
                + " AND st.sg_oficina = o.id  "
                //////                + " ANd o.id = " + idOficina
                + " AND o.sg_direccion = d.id"
                + " group by d.ciudad, st.nombre having count(*) > 0");

        UtilLog4j.log.debug(this, new StringBuilder().append("Q Hospedados staff: ").append(q.toString()).toString());
        return q.getResultList();
    }

    //Saida hotel
    
    public List traerSalidaHuespedNativo() {
        manana = siManejoFechaLocal.fechaSumarDias(new Date(), 1);
        UtilLog4j.log.debug(this, new StringBuilder().append("Manana ES: ").append(Constantes.FMT_yyyyMMdd.format(manana)).toString());
        Query q = em.createNativeQuery("select d.ciudad, st.nombre, count(*) "
                + " from sg_huesped_staff hs, sg_solicitud_estancia se, sg_staff_habitacion sh, sg_staff st, sg_oficina o, sg_direccion d"
                + " where hs.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND hs.cancelado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND hs.hospedado = '" + Constantes.BOOLEAN_TRUE + "'"
                + " AND hs.fecha_Salida = cast('" + Constantes.FMT_yyyyMMdd.format(manana) + "' as date)"
                + " AND hs.sg_solicitud_estancia = se.id"
                + " AND hs.sg_staff_habitacion = sh.id "
                + " AND sh.sg_staff = st.id  "
                + " AND st.sg_oficina = o.id  "
                //////                + " ANd o.id = " + idOficina
                + " AND o.sg_direccion = d.id"
                + " group by d.ciudad, st.nombre having count(*) > 0");

        UtilLog4j.log.debug(this, new StringBuilder().append("query").append(q.toString()).toString());

        return q.getResultList();
    }

    //Saida hotel
    
    public List traerCanceladoHuespedNativo() {
        Query q = em.createNativeQuery("select d.ciudad,  st.nombre, count(*) "
                + " from sg_huesped_staff hs, sg_solicitud_estancia se, sg_staff_habitacion sh, sg_staff st, sg_oficina o, sg_direccion d"
                + " where hs.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND hs.cancelado = '" + Constantes.BOOLEAN_TRUE + "'"
                + " AND hs.hospedado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND hs.fecha_modifico = cast('" + Constantes.FMT_yyyyMMdd.format(new Date()) + "' as date)"
                + " AND hs.sg_solicitud_estancia = se.id"
                + " AND hs.sg_staff_habitacion = sh.id "
                + " AND sh.sg_staff = st.id  "
                + " AND st.sg_oficina = o.id  "
                ////////                + " AND o.id = " +idOficina
                + " AND o.sg_direccion = d.id"
                + " group by d.ciudad,  st.nombre having count(*) > 0");

        UtilLog4j.log.debug(this, new StringBuilder().append("Q cancelados staff: ").append(q.toString()).toString());
        return q.getResultList();
    }

    
    public List<SgHuespedStaffVo> getAllSgHuespedStaffBySgStaffHabitacion(int idSgStaffHabitacion) {
        UtilLog4j.log.debug(this, "SgHuespedStaffImpl.getAllSgHuespedStaffBySgStaffHabitacion()");

        clearQuery();

        appendQuery("SELECT hs.ID AS ID_SG_HUESPED_STAFF, ");//0
        appendQuery("hs.SG_STAFF_HABITACION AS ID_SG_STAFF_HABITACION, "); //1
        appendQuery("CASE WHEN (SELECT dse.USUARIO FROM SG_DETALLE_SOLICITUD_ESTANCIA dse WHERE dse.ID=hs.SG_DETALLE_SOLICITUD_ESTANCIA) IS NULL THEN (SELECT i.NOMBRE FROM SG_INVITADO i WHERE i.ID=(SELECT dse.SG_INVITADO FROM SG_DETALLE_SOLICITUD_ESTANCIA dse WHERE dse.ID=hs.SG_DETALLE_SOLICITUD_ESTANCIA)) "); //2
        appendQuery("WHEN (SELECT dse.USUARIO FROM SG_DETALLE_SOLICITUD_ESTANCIA dse WHERE dse.ID=hs.SG_DETALLE_SOLICITUD_ESTANCIA) IS NOT NULL THEN (SELECT u.NOMBRE FROM USUARIO u WHERE u.ID=(SELECT dse.USUARIO FROM SG_DETALLE_SOLICITUD_ESTANCIA dse WHERE dse.ID=hs.SG_DETALLE_SOLICITUD_ESTANCIA)) ");
        appendQuery("END AS NOMBRE_SG_DETALLE_SOL_ESTANCIA ");
        appendQuery("FROM SG_HUESPED_STAFF hs ");
        appendQuery("WHERE ");
        appendQuery("hs.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("' ");
        appendQuery("AND hs.SG_STAFF_HABITACION=").append(idSgStaffHabitacion).append(" ");
        appendQuery("AND hs.HOSPEDADO='").append(Constantes.BOOLEAN_TRUE).append("'");

        UtilLog4j.log.debug(this, new StringBuilder().append("query: ").append(getStringQuery()).toString());

        SgHuespedStaffVo vo;
        List<Object[]> result = em.createNativeQuery(getStringQuery()).getResultList();
        List<SgHuespedStaffVo> list = new ArrayList<SgHuespedStaffVo>();

        for (Object[] objects : result) {
            vo = new SgHuespedStaffVo();
            vo.setId((Integer) objects[0]);
            vo.setIdSgStaffHabitacion((Integer) objects[1]);
            vo.setNombreHuesped((String) objects[2]);
            list.add(vo);
        }

        UtilLog4j.log.debug(this, new StringBuilder().append("Se encontraron ").append(list != null ? list.size() : 0).append(" SgHuespedStaff").toString());

        return (list != null ? list : Collections.EMPTY_LIST);
    }
    
    
    public List<HuespedVo> traerHuespedesStaffQueSalenHoyPorSolicitud(int idSolicitud) {
        List<HuespedVo> list = null;
        try {
            clearQuery();

            appendQuery(" Select hstaff.id,");//0
            appendQuery(" hstaff.SG_SOLICITUD_ESTANCIA,");//1
            appendQuery(" hstaff.FECHA_GENERO,");//2
            appendQuery(" hstaff.FECHA_INGRESO,");//3
            appendQuery(" hstaff.FECHA_REAL_INGRESO,");//4
            appendQuery(" hstaff.FECHA_SALIDA,");//5
            appendQuery(" hstaff.FECHA_REAL_SALIDA,");//6
            appendQuery(" hab.numero_habitacion ,");//7
            appendQuery(" hstaff.SG_DETALLE_SOLICITUD_ESTANCIA,");//8
            appendQuery(" huesped.USUARIO,");//9

            appendQuery(" case when huesped.sg_invitado is not null then");
            appendQuery(" huesped.sg_invitado ");
            appendQuery(" else 0 end, ");//10

            appendQuery(" case when huesped.USUARIO is not null then ");
            appendQuery(" (select u.nombre From usuario u where u.id = huesped.USUARIO)");
            appendQuery(" else (select i.nombre From usuario i where i.id = huesped.sg_invitado)");
            appendQuery(" end as usuario,");//11

            appendQuery(" case when huesped.USUARIO is not null then ");
            appendQuery(" 'true'");
            appendQuery(" else 'false'");
            appendQuery(" end as isUsuario,");//12

            appendQuery(" case when huesped.USUARIO is not null then ");
            appendQuery(" (select u.EMAIL From usuario u where u.id = huesped.USUARIO)");
            appendQuery(" else ''");
            appendQuery(" end as correo,");//13

            appendQuery(" staff.NOMBRE,");//14
            appendQuery(" hab.NOMBRE,");//15
            appendQuery(" esp.NOMBRE");//16

            appendQuery(" From SG_HUESPED_STAFF hstaff,");
            appendQuery(" SG_DETALLE_SOLICITUD_ESTANCIA huesped,");
            appendQuery(" SG_STAFF_HABITACION hab,");
            appendQuery(" SG_STAFF staff,");
            appendQuery(" SG_TIPO_ESPECIFICO esp");

            appendQuery(" where hstaff.HOSPEDADO = 'True'");
            appendQuery(" and hstaff.CANCELADO = 'False'");
            appendQuery(" and hstaff.ELIMINADO = 'False'");
            appendQuery(" and hstaff.SG_DETALLE_SOLICITUD_ESTANCIA = huesped.ID");
            appendQuery(" and hstaff.SG_STAFF_HABITACION = hab.id");
            appendQuery(" and hab.SG_STAFF = staff.id");
            appendQuery(" and esp.id = huesped.SG_TIPO_ESPECIFICO");
            appendQuery(" and hstaff.SG_SOLICITUD_ESTANCIA = ");
            appendQuery(idSolicitud);
            appendQuery(" and (hstaff.FECHA_SALIDA = dateadd(1 day to cast('now' as Date)))");
            // or hstaff.FECHA_SALIDA = cast('now' as Date)
            log("-----------------------------" + getStringQuery());
            List<Object[]> lista = em.createNativeQuery(getStringQuery()).getResultList();
            if (lista != null && !lista.isEmpty()) {
                list = new ArrayList<HuespedVo>();
                for (Object[] objects : lista) {
                    list.add(castHuespedVo(objects));
                }

            }
            return list;
        } catch (Exception e) {
            log("Exception al obtener los huespedes en hotel " + e.getMessage());
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
        vo.setIdUsuario((String) objects[9]);

        vo.setIdInvitado((Integer) objects[10]);
        vo.setNombreHuesped((String) objects[11]);
        vo.setInvitado(Boolean.parseBoolean((String) objects[12]));
        vo.setEmailHuesped((String) objects[13]); //Correo del usuario (No invitado)
        vo.setNombreSgStaff((String) objects[14]);//nombre del usuario o el invitado
        vo.setNombreHabitacion((String) objects[15]);
        vo.setTipohHuesped((String) objects[16]);

        return vo;
    }

    
    public List<SgHuespedStaffVo> traerHuespedStaffPorFechaSalidaHoy(int idStaff, boolean excluirPeriodoPrueba) {
        List<SgHuespedStaffVo> list = null;
        SgHuespedStaffVo vo;
        String comodin = "";
        if (excluirPeriodoPrueba) {
            comodin = " and s.SG_TIPO_ESPECIFICO <> 15";
        }
        try {
            clearQuery();
            appendQuery(" select s.ID,");//0
            appendQuery(" s.SG_DETALLE_SOLICITUD_ESTANCIA,");//1
            appendQuery(" s.SG_STAFF_HABITACION,");//2
            appendQuery(" s.SG_SOLICITUD_ESTANCIA,");//3
            appendQuery(" sh.NOMBRE,");//4
            appendQuery(" s.SG_TIPO,");//5
            appendQuery(" s.SG_TIPO_ESPECIFICO,");//6
            appendQuery(" s.FECHA_INGRESO,");//7
            appendQuery(" s.FECHA_SALIDA");//8
            appendQuery(" From SG_HUESPED_STAFF s,SG_STAFF_HABITACION sh");
            appendQuery(" Where s.HOSPEDADO = 'True'");
            appendQuery(" and sh.SG_STAFF = ");
            appendQuery(idStaff);
            appendQuery(" and s.ELIMINADO='False' ");
            appendQuery(" and s.CANCELADO='False' ");
            appendQuery(" and (s.FECHA_SALIDA = cast('now' as Date))");
            appendQuery(comodin);
            appendQuery(" and s.SG_staff_HABITACION = sh.ID");

            List<Object[]> lista = em.createNativeQuery(getStringQuery()).getResultList();
            if (lista != null && !lista.isEmpty()) {
                log("se encontraron " + lista.size() + " de objetos de huespedes en staff");
                list = new ArrayList<SgHuespedStaffVo>();
                for (Object[] objects : lista) {
                    vo = new SgHuespedStaffVo();
                    vo.setId((Integer) objects[0]);
                    vo.setIdSgDetalleSolicitudEstancia((Integer) objects[1]);
                    vo.setIdSgStaffHabitacion((Integer) objects[2]);
                    vo.setIdSgSolicitudEstancia((Integer) objects[3]);
                    vo.setNombreSgStaffHabitacion((String) objects[4]);
                    vo.setIdSgTipo((Integer) objects[5]);
                    vo.setIdSgTipoEspecifico((Integer) objects[6]);
                    vo.setFechaIngreso((Date) objects[7]);
                    vo.setFechaSalida((Date) objects[8]);
                    list.add(vo);
                }
            }
            return list;
        } catch (Exception e) {
            log("Exception al obtener los huespedes en staff " + e.getMessage());
            return null;
        }
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    
    public List<SgHuespedStaffVo> traerTotalHospedados(int anio) {
        clearQuery();
        List<SgHuespedStaffVo> lhuesped = new ArrayList<SgHuespedStaffVo>();
        query.append("select  o.NOMBRE, count(hs.id) from SG_HUESPED_STAFF hs ");
        query.append("      inner join SG_SOLICITUD_ESTANCIA se on hs.SG_SOLICITUD_ESTANCIA = se.ID");
        query.append("      inner join SG_STAFF_HABITACION hab on hs.SG_STAFF_HABITACION = hab.ID");
        query.append("      inner join SG_STAFF st on hab.SG_STAFF = st.ID");
        query.append("      inner join SG_OFICINA o on st.SG_OFICINA = o.ID");
        query.append("  where hs.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        if (anio > 0) {
            query.append("  and extract(year from hs.fecha_real_ingreso) = ").append(anio);
        }
        query.append("  and hs.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("  group by o.NOMBRE");
        //
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lo) {
            lhuesped.add(castTotalHuespedVo(objects));
        }
        return lhuesped;
    }

    
    public List<SgHuespedStaffVo> traerTotalHospedadosAnio(int oficina, int anio) {
        clearQuery();
        List<SgHuespedStaffVo> lhuesped = new ArrayList<>();
        query.append("select  SUBSTRING(to_char(hs.fecha_real_ingreso, 'DD/MM/YYYY'), 4, 10), count(hs.id) from SG_HUESPED_STAFF hs ");
        query.append("      inner join SG_SOLICITUD_ESTANCIA se on hs.SG_SOLICITUD_ESTANCIA = se.ID");
        query.append("      inner join SG_STAFF_HABITACION hab on hs.SG_STAFF_HABITACION = hab.ID");
        query.append("      inner join SG_STAFF st on hab.SG_STAFF = st.ID");
        query.append("      inner join SG_OFICINA o on st.SG_OFICINA = o.ID");
        query.append("  where o.id = ").append(oficina);
        if (anio > 0) {
            query.append("  and extract(year from hs.fecha_real_ingreso) = ").append(anio);
        }
        query.append("  and hs.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and hs.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("  group by SUBSTRING(to_char(hs.fecha_real_ingreso, 'DD/MM/YYYY'), 4, 10)");
        //
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lo) {
            lhuesped.add(castTotalHuespedVo(objects));
        }
        return lhuesped;
    }

    
    public List<SgHuespedStaffVo> traerTotalHospedadosAnioStaffH(int oficina, int anio, int mes) {
        clearQuery();
        List<SgHuespedStaffVo> lhuesped = new ArrayList<>();
        query.append("select  st.nombre, count(hs.id) from SG_HUESPED_STAFF hs ");
        query.append("      inner join SG_SOLICITUD_ESTANCIA se on hs.SG_SOLICITUD_ESTANCIA = se.ID");
        query.append("      inner join SG_STAFF_HABITACION hab on hs.SG_STAFF_HABITACION = hab.ID");
        query.append("      inner join SG_STAFF st on hab.SG_STAFF = st.ID");
        query.append("  where st.sg_oficina = ").append(oficina);
        if (anio > 0) {
            query.append("  and extract(year from hs.fecha_real_ingreso) = ").append(anio);
        }
        if (mes > 0) {
            query.append("  and extract(month from hs.fecha_real_ingreso) = ").append(mes);
        }
        query.append("  and hs.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and hs.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("  group by st.nombre");
        //
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lo) {
            lhuesped.add(castTotalHuespedVo(objects));
        }
        return lhuesped;
    }

    private SgHuespedStaffVo castTotalHuespedVo(Object[] objects) {
        SgHuespedStaffVo staffVo = new SgHuespedStaffVo();
        staffVo.setOficina((String) objects[0]);
        staffVo.setTotal((Long) objects[1]);
        return staffVo;
    }

    
    public List<HuespedVo> traerHospedados(int oficina, String staff, String inicio, String fin) {
        clearQuery();
        List<HuespedVo> lhuesped = new ArrayList<>();
        query.append("select u.NOMBRE, inv.NOMBRE,  st.NOMBRE, hs.FECHA_REAL_INGRESO, hs.FECHA_REAL_SALIDA, hab.NUMERO_HABITACION, g.nombre");
        query.append("  from SG_HUESPED_STAFF hs");
        query.append("      inner join SG_SOLICITUD_ESTANCIA se on hs.SG_SOLICITUD_ESTANCIA = se.ID");
        query.append("      inner join SG_STAFF_HABITACION hab on hs.SG_STAFF_HABITACION = hab.ID");
        query.append("      inner join SG_STAFF st on hab.SG_STAFF = st.ID");
        query.append("      inner join SG_DETALLE_SOLICITUD_ESTANCIA dse on hs.SG_DETALLE_SOLICITUD_ESTANCIA  = dse.ID");
        query.append("      left join USUARIO u on dse.USUARIO = u.ID");
        query.append("      left join SG_INVITADO inv on dse.SG_INVITADO = inv.ID");
        query.append("      inner join gerencia g on se.gerencia = g.id");
        query.append("  where hs.FECHA_INGRESO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
        query.append("  and se.SG_OFICINA = ").append(oficina);
        query.append("  and st.nombre = '").append(staff).append("'");
        query.append("  and hs.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and hs.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  order by u.NOMBRE asc");
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
        huespedVo.setNombreSgStaff((String) objects[2]);
        huespedVo.setFechaIngreso((Date) objects[3]);
        huespedVo.setFechaSalida((Date) objects[4]);
        huespedVo.setNumeroSgStaffHabitacion((String) objects[5]);
        huespedVo.setNombreGerencia((String) objects[6]);
        huespedVo.setIdAdjunto(0);
        huespedVo.setAdjuntoUUID("N/A");

        return huespedVo;
    }

    
    public List<HuespedVo> traerHospedados(int idOficina, String nombre, String inicio) {
        clearQuery();
        String[] cad = inicio.split("/");
        List<HuespedVo> lhuesped = new ArrayList<>();
        query.append("select u.NOMBRE, inv.NOMBRE,  st.NOMBRE, hs.FECHA_REAL_INGRESO, hs.FECHA_REAL_SALIDA, hab.NUMERO_HABITACION, g.nombre");
        query.append("  from SG_HUESPED_STAFF hs");
        query.append("      inner join SG_SOLICITUD_ESTANCIA se on hs.SG_SOLICITUD_ESTANCIA = se.ID");
        query.append("      inner join SG_STAFF_HABITACION hab on hs.SG_STAFF_HABITACION = hab.ID");
        query.append("      inner join SG_STAFF st on hab.SG_STAFF = st.ID");
        query.append("      inner join SG_DETALLE_SOLICITUD_ESTANCIA dse on hs.SG_DETALLE_SOLICITUD_ESTANCIA  = dse.ID");
        query.append("      left join USUARIO u on dse.USUARIO = u.ID");
        query.append("      left join SG_INVITADO inv on dse.SG_INVITADO = inv.ID");
        query.append("      inner join gerencia g on se.gerencia = g.id");
        query.append("  where se.SG_OFICINA = ").append(idOficina);
        query.append("  and st.nombre = '").append(nombre).append("'");
        query.append("  and extract(month from hs.FECHA_REAL_INGRESO) = ").append(cad[0]);

        query.append("  and extract(year from hs.FECHA_REAL_INGRESO) = ").append(cad[1]);
        query.append("  and hs.CANCELADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and hs.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  order by u.NOMBRE asc");
      //
        //
        List<Object[]> lobj = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] objects : lobj) {
            lhuesped.add(castReporteHuespedVo(objects));
        }
        return lhuesped;
    }
}
