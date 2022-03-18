
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.CoNoticia;
import sia.modelo.Compania;
import sia.modelo.Estatus;
import sia.modelo.SgDetalleRutaTerrestre;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgVehiculo;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeCiudad;
import sia.modelo.SgViajeVehiculo;
import sia.modelo.SgViajero;
import sia.modelo.SiAdjunto;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.gr.vo.GrIntercepcionVO;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoComentarioImpl;
import sia.servicios.comunicacion.impl.CoCompartidaImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgViajeCiudadImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless
public class SgViajeImpl extends AbstractFacade<SgViaje> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    DSLContext dbCtx;

    public SgViajeImpl() {
        super(SgViaje.class);
    }
    @Inject
    private SgViajeSiMovimientoImpl sgViajeSiMovimientoRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private NotificacionViajeImpl notificacionViajeRemote;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoRemote;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private EstatusImpl estatusRemote;
    @Inject
    private FolioImpl folioRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreRemote;
    @Inject
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private CoNoticiaImpl coNoticiaService;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SiUsuarioTipoImpl siUsuarioCopiadoRemote;
    @Inject
    private SgViajeCiudadImpl sgViajeCiudadRemote;
    @Inject
    private SgVehiculoImpl sgVehiculoRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SgItinerarioImpl sgItinerarioRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SgInvitadoImpl sgInvitadoRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgViajeroSiMovimientoImpl sgViajeroSiMovimientoRemote;
    @Inject
    private CoComentarioImpl coComentarioRemote;
    @Inject
    private SiParametroImpl siParametroRemote;
    @Inject
    private CompaniaImpl companiaRemote;
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;
    @Inject
    private CoCompartidaImpl coCompartidaRemote;
    private String queryBaseViajesSalida;
    private String queryBaseViajesSalidaAereos;

    private Usuario getResponsableByGerencia(int idGerencia) {
        UtilLog4j.log.info(this, "getResponsableByGerencia");
        return this.gerenciaRemote.getResponsableByApCampoAndGerencia(1, idGerencia, false);
    }

    public ViajeVO findByCodigo(String codigo) {
        UtilLog4j.log.info(this, "SgViajeImpl.findByCodigo()");
        clearQuery();
        try {
            query.append(consultaViaje());
            query.append(" WHERE   v.codigo = '").append(codigo).append("'");
            query.append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] result = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castViajeBuscar(result, true, false, false);
        } catch (Exception nure) {
            UtilLog4j.log.info(this, nure.getMessage());
            return null;
        }
    }

    public SgViaje findCodigo(String codigo) {
        try {
            return (SgViaje) em.createQuery("SELECT v FROM SgViaje v WHERE v.codigo = :codigo AND v.eliminado = :FALSE").setParameter("codigo", codigo).setParameter("FALSE", Constantes.BOOLEAN_FALSE).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Expcecion al buscar el viaje" + e.getMessage());
            return null;
        }
    }

    public SgViaje findSgViajeVuelta(int idSgViajeIda) {
        UtilLog4j.log.info(this, "SgViajeImpl.findSgViajeVuelta()");

//        SgViaje sgViajeIda = find(idSgViajeIda);
        SgViaje sgViajeVuelta = null;

        try {
            sgViajeVuelta = (SgViaje) em.createQuery("SELECT v FROM SgViaje v WHERE v.sgViaje.id = :idViajeIda AND v.eliminado = :eliminado").setParameter("idViajeIda", idSgViajeIda).setParameter("eliminado", Constantes.NO_ELIMINADO).getSingleResult();
        } catch (NoResultException nre) {
            UtilLog4j.log.info(this, nre.getMessage());
        } catch (NonUniqueResultException nure) {
            UtilLog4j.log.info(this, nure.getMessage());
        }

        UtilLog4j.log.info(this, sgViajeVuelta != null ? ("Se encontró viaje de regreso para el viaje (id): " + idSgViajeIda) : ("No se encontró viaje de regreso para el viaje (id): " + idSgViajeIda));

        return sgViajeVuelta;
    }

    public SgViaje guardarViajeAereo(int sgOficina, Usuario usuario, SgViaje sgViaje, int idSolicitudViaje,
            List<ViajeroVO> listaViajero, ItinerarioCompletoVo sgItinerario, String redondo) {
        try {
            boolean notificar = false;
            boolean v = false;
            if (usuario.getId().equals("PRUEBA")) {
                sgViaje.setCodigo("PVI" + getDigitosAño(new Date()) + "-" + String.valueOf(folioRemote.getFolio(Constantes.CONSECUTIVO_VIAJE_PRUEBA)));
                UtilLog4j.log.info(this, "dentro del folio " + sgViaje.getCodigo());
            } else {
                sgViaje.setCodigo("VI" + getDigitosAño(new Date()) + "-" + String.valueOf(folioRemote.getFolio(Constantes.CONSECUTIVO_VIAJE)));
            }
            sgViaje.setSgOficina(sgOficinaRemote.find(sgOficina));
            sgViaje.setAutobus(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoPropio(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoAsignadoEmpresa(Constantes.BOOLEAN_FALSE);
            sgViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(3));
            sgViaje.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_POR_SALIR));
            sgViaje.setSgItinerario(sgItinerarioRemote.find(sgItinerario.getId()));
            sgViaje.setGenero(usuario);
            sgViaje.setFechaGenero(new Date());
            sgViaje.setHoraGenero(new Date());
            sgViaje.setEliminado(Constantes.NO_ELIMINADO);
            sgViaje.setUsuarioRegresaViaje(usuario);

            SolicitudViajeVO solicitudViaje = sgSolicitudViajeRemote.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO, Constantes.CERO);
            UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, solicitudViaje.getIdGerencia());
            //
            if (redondo.equals(Constantes.redondo)) {
                sgViaje.setRedondo(Constantes.BOOLEAN_TRUE);
            } else {
                sgViaje.setFechaRegreso(null);
                sgViaje.setHoraRegreso(null);
                sgViaje.setRedondo(Constantes.BOOLEAN_FALSE);
            }
            int idViajeIda = sgViaje.getSgViaje() == null ? 0 : sgViaje.getSgViaje().getId();
            v = notificacionViajeRemote.sendMailAirTravel(usuarioResponsableGerenciaVo.getEmailUsuario(),
                    correoCopia(usuario.getEmail(), solicitudViaje),
                    solicitudViaje,
                    listaViajero,
                    sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(),
                    sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(),
                    sgViaje.isRedondo(), idViajeIda, sgViaje.getCodigo(), usuarioResponsableGerenciaVo.getNombreUsuario());

            if (v) {
                create(sgViaje);
                //Publica noticia
                createEventNews(sgViaje.getId(), usuario.getId());
            }

            for (ViajeroVO Viajero : listaViajero) {
                SgViajero sgViajero = sgViajeroRemote.find(Viajero.getId());
                sgViajeroRemote.agregarViaje(usuario.getId(), sgViajero, sgViaje, notificar);
            }
            //Finaliza lasolicitus

            sgEstatusAprobacionRemote.finalizeRequest(solicitudViaje.getIdSolicitud(), usuario, 0);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exc: guardar viaje aereo: " + e.getMessage() + "  appendQuery(appendQuery(appendQuery(" + e.getCause().toString());
            return null;
        }
        return sgViaje;
    }

    /**
     * MLUIS
     *
     * @param sgOficina
     * @param sesion
     * @param viajeVO
     * @param idSolicitudViaje
     * @param listaViajero
     * @param sgItinerario
     * @return
     */
    public int guardarViajeAereoRegreso(int sgOficina, String sesion, ViajeVO viajeVO, int idSolicitudViaje,
            List<ViajeroVO> listaViajero, ItinerarioCompletoVo sgItinerario) {
        try {
            boolean v = false;

            SolicitudViajeVO svvo = sgSolicitudViajeRemote.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO, Constantes.CERO);
            ItinerarioCompletoVo itinerarioVuelta = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitudViaje, false, true, "id");

            SgViaje sgViaje = new SgViaje();
            //
            sgViaje.setFechaProgramada(itinerarioVuelta.getEscalas().get(0).getFechaSalida());
            sgViaje.setHoraProgramada(itinerarioVuelta.getEscalas().get(0).getHoraSalida());
            sgViaje.setFechaSalida(itinerarioVuelta.getEscalas().get(0).getFechaSalida());
            sgViaje.setHoraSalida(itinerarioVuelta.getEscalas().get(0).getHoraSalida());
            //
            sgViaje.setRedondo(Constantes.BOOLEAN_FALSE);

            sgViaje.setCodigo("VI" + getDigitosAño(new Date()) + "-" + String.valueOf(folioRemote.getFolio(Constantes.CONSECUTIVO_VIAJE)));
            sgViaje.setSgOficina(sgOficinaRemote.find(sgOficina));
            sgViaje.setAutobus(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoPropio(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoAsignadoEmpresa(Constantes.BOOLEAN_FALSE);

            sgViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(3));
            sgViaje.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_POR_SALIR));
            sgViaje.setSgItinerario(sgItinerarioRemote.find(itinerarioVuelta.getId()));
            sgViaje.setGenero(new Usuario(sesion));
            sgViaje.setFechaGenero(new Date());
            sgViaje.setHoraGenero(new Date());
            sgViaje.setEliminado(Constantes.NO_ELIMINADO);
            //sgViaje.setUsuarioRegresaViaje(new Usuario(sesion));
            //
            sgViaje.setSgViaje(find(viajeVO.getId()));
            UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, svvo.getIdGerencia());
            //
            sgViaje.setResponsable(usuarioRemote.find(usuarioResponsableGerenciaVo.getIdUsuario()));
            // int idViajeIda = sgViaje.getSgViaje() == null ? 0 : sgViaje.getSgViaje().getId();
            v = notificacionViajeRemote.sendMailAirTravel(usuarioResponsableGerenciaVo.getEmailUsuario(),
                    correoCopia(sgViaje.getGenero().getEmail(), svvo), svvo, viajeVO.getListaViajeros(),
                    sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(),
                    sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(), sgViaje.isRedondo(),
                    viajeVO.getId(), sgViaje.getCodigo(), usuarioResponsableGerenciaVo.getNombreUsuario());
            if (v) {
                create(sgViaje);
                //Publica noticia
                createEventNews(sgViaje.getId(), sesion);

                for (ViajeroVO Viajero : viajeVO.getListaViajeros()) {
                    SgViajero sgViajero = sgViajeroRemote.find(Viajero.getId());
                    sgViajeroRemote.agregarViaje(sesion, sgViajero, sgViaje, false);
                }
                finalizarViaje(viajeVO.getId(), sesion);
            }
            return sgViaje.getId();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "excepción al guardar el viaje aéreo regreso: " + e.getMessage() + " appendQuery(appendQuery(appendQuery(appendQuery(" + e.getCause().toString());
            return 0;
        }
    }

    /**
     * Modifico: NLopez 07/11/2013 El adquirir correo
     *
     * @param usuario
     * @param responsable
     * @param vehiculoVO
     * @param listSolViajeTemp
     * @param listaViajero
     * @param sgOficina
     * @param opcionSeleccionada
     * @param idRuta
     * @param fechaSalida
     * @param fechaRegreso
     * @param horaSalida
     * @param horaRegreso
     * @param correoCopia
     * @param redondo
     * @param telefono
     * @return
     */
    public SgViaje saveCompanyCar(Usuario usuario, Usuario responsable, VehiculoVO vehiculoVO, List<SolicitudViajeVO> listSolViajeTemp,
            List<ViajeroVO> listaViajero, int sgOficina, String opcionSeleccionada, int idRuta, Date fechaSalida,
            Date fechaRegreso, Date horaSalida, Date horaRegreso, String correoCopia, String redondo, String telefono) {
        UtilLog4j.log.info(this, "SgViajeImpl.saveCompanyCar()");

        //actualiza el telefono
        if (responsable.getTelefono().isEmpty() || responsable.getTelefono() == null) {
            usuarioRemote.agregaTelefonoUsuario(responsable.getId(), telefono, usuario.getId());
        }

        SgViaje sgViaje = null;
        //vaiables para el correo
        String para = correoCopia;
        String cc = "";
        //----------------
        boolean notificar = false;
        boolean v = false;

        //Guardar viaje
        sgViaje = guardar(sgOficina, responsable, opcionSeleccionada, usuario, idRuta, fechaSalida, fechaRegreso,
                horaSalida, horaRegreso, redondo);
        SgVehiculo sgVehiculo = sgVehiculoRemote.find(vehiculoVO.getId());
        UtilLog4j.log.info(this, "guardar viaje");
        try {

            v = validarViajesTerrestres(usuario, sgViaje, listaViajero, vehiculoVO);
            if (v) {

                create(sgViaje);
                SgViaje newSgViaje = findCodigo(sgViaje.getCodigo());
                //Guardar en Viaje-Vehiculo
                sgViajeVehiculoRemote.save(usuario, sgVehiculo, newSgViaje);
                //Guarda los viajeros del viaje
                for (ViajeroVO Viajero : listaViajero) {
                    sgViajeroRemote.agregarViaje(usuario.getId(), sgViajeroRemote.find(Viajero.getId()), newSgViaje, notificar);
                }
                UtilLog4j.log.info(this, "Publicar noticia para el viaje " + newSgViaje.getId());
                createEventNewsByListViajero(newSgViaje.getId(), usuario.getId(), listaViajero, false);

            }
        } catch (Exception ex) {

            UtilLog4j.log.info(this, ex.getMessage());
            UtilLog4j.log.info(ex);
        }
        return sgViaje;
    }

    /**
     *
     * Modifico: NLopez 07/11/2013 El adquirir correo
     *
     * @param usuario
     * @param responsable
     * @param vehiculoVO
     * @param idSolicitudViaje
     * @param sgOficina
     * @param opcionSeleccionada
     * @param idRuta
     * @param fechaSalida
     * @param fechaRegreso
     * @param horaSalida
     * @param horaRegreso
     * @param redondo
     * @param lv
     * @param telefono
     * @return
     */
    public SgViaje saveCompanyCarCiudad(Usuario usuario, Usuario responsable, VehiculoVO vehiculoVO, Integer idSolicitudViaje,
            int sgOficina, String opcionSeleccionada, int idRuta, Date fechaSalida, Date fechaRegreso, Date horaSalida,
            Date horaRegreso, String redondo, List<ViajeroVO> lv, String telefono) {
        UtilLog4j.log.info(this, "SgViajeImpl.saveCompanyCarCiudad()");

        SgViaje sgViaje = null;
        SolicitudViajeVO solicitudViaje = sgSolicitudViajeRemote.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO, Constantes.CERO);
//        ViajeDestinoVo destinoSolicitudVo = null;
        SgViajeCiudad sgViajeCiudad = null;
        String para;
        String cc = "";
        boolean notificar = false;
        boolean v = false;
        UsuarioResponsableGerenciaVo ugv = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, solicitudViaje.getIdGerencia());
        //actualiza el telefono
        if (responsable == null) {
            int cont = 0;
            for (ViajeroVO viajeroVO : lv) {
                if (viajeroVO.getIdInvitado() == 0) {
                    usuarioRemote.agregaTelefonoUsuario(viajeroVO.getIdUsuario(), viajeroVO.getTelefono(), usuario.getId());
                    //
                    responsable = usuarioRemote.find(viajeroVO.getIdUsuario());
                    cont++;
                    break;
                }
            }
            if (cont == 0) {
                responsable = usuarioRemote.find(ugv.getIdUsuario());
            }
        }

        //
        sgViaje = guardar(sgOficina, responsable, opcionSeleccionada, usuario, idRuta, fechaSalida, fechaRegreso, horaSalida, horaRegreso,
                redondo);

        if (solicitudViaje != null) {
            //buscar el destino de la soliciitud
            UtilLog4j.log.info(this, "Sollicitud es != null");
//            destinoSolicitudVo = this.sgViajeCiudadRemote.findDestinoSolicitudViaje(idSolicitudViaje);

            sgViajeCiudad = this.sgViajeCiudadRemote.findSgViajeCiudadBySgSolicitudViaje(idSolicitudViaje);
            UtilLog4j.log.info(this, "AQUI ESTOY!!!");
            UtilLog4j.log.info(this, "destno " + sgViajeCiudad.getSiCiudad().getNombre());
            sgViaje.setSgViajeCiudad(sgViajeCiudad);
        }
        //Envío de e-mail por SV para cada Gerente de la SV con copia a los Viajeros
        //
        List<UsuarioVO> lu = usuarioRemote.getUsuariosByRol(Constantes.SGL_RESPONSABLE);
        //lu.addAll(usuarioRemote.getUsuariosByRol(Constantes.SGL_SEGURIDAD));
        for (UsuarioVO uvo : usuarioRemote.getUsuariosByRol(Constantes.SGL_ANALISTA)) {
            if (uvo.getId().equals(sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(Constantes.ID_OFICINA_TORRE_MARTEL).getIdAnalista())) {
                lu.add(uvo);
            }
        }
        int nlist = lu.size();
        int x = 1;
        para = usuario.getEmail() + ",";
        for (UsuarioVO usuario1 : lu) {
            para += (usuario1.getMail());
            if (x < nlist) {
                para += ", ";
            }
            x++;
        }

        try {
            //Guardar viaje
            v = validarViajesTerrestres(usuario, sgViaje, lv, vehiculoVO);
            if (v) {
                if (sgViaje.getSgViajeCiudad() != null) {
                    UtilLog4j.log.info(this, "Destino != null");
                    //buscar siCiudad y crear la realcion
                    create(sgViaje);
                    SgViaje newSgViaje = sgViajeRemote.findCodigo(sgViaje.getCodigo());
                    //BUSCAR VIAJE PERSISTIDO
                    if (opcionSeleccionada.equals(Constantes.VEHICULO_EMPRESA)) {
                        //Guardar en Viaje-Vehiculo
                        sgViajeVehiculoRemote.save(usuario, sgVehiculoRemote.find(vehiculoVO.getId()), newSgViaje);
                    }
                    //Guarda los viajeros del viaje
                    for (ViajeroVO Viajero : lv) {
                        SgViajero sgViajero = sgViajeroRemote.find(Viajero.getId());
                        sgViajeroRemote.agregarViaje(usuario.getId(), sgViajero, newSgViaje, notificar);
                    }
                    UtilLog4j.log.info(this, "Todo Ok");
                    createEventNewsByListViajero(newSgViaje.getId(), usuario.getId(), lv, false);
                    //Crea el estado del semaforo
                    //sgViajeEstadoSemaforoRemote.guardarViajeSemaforo(newSgViaje.getId(), idEstadoSemaforo, usuario.getId());
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.info(this, "Excepcion al guardar el viaje a ciudad " + ex.getMessage());
            UtilLog4j.log.info(ex);
        }
        return sgViaje;
    }

    public SgViaje noCompanyCar(int sgOficina, ViajeroVO viajero, String opcionSeleccionada, Usuario usuario, int idRuta, Date fechaSalida,
            Date fechaRegreso, Date horaSalida, Date horaRegreso, String correoCopia,
            String redondo, String telefono) {
        boolean v = false;
        SgViaje sgViaje = null;
        String correoGerencia = "";
        Usuario res;
        SgViajero sgViajero = sgViajeroRemote.find(viajero.getId());
        SolicitudViajeVO solicitudViajeVO = sgSolicitudViajeRemote.buscarPorId(sgViajero.getSgSolicitudViaje().getId(), Constantes.NO_ELIMINADO, Constantes.CERO);
        UsuarioResponsableGerenciaVo urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(1, solicitudViajeVO.getIdGerencia());
        //Prepara el viaje
        if (sgViajero.getUsuario() != null) {
            UtilLog4j.log.info(this, "usuario: " + sgViajero.getUsuario().getNombre());
            UtilLog4j.log.info(this, "dentro de user");
            res = sgViajero.getUsuario();
            correoGerencia = sgViajero.getUsuario().getEmail();
            correoGerencia += "," + getResponsableByGerencia(sgViajero.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail();
        } else {
            UtilLog4j.log.info(this, "dentro de invitado");
            res = getResponsableByGerencia(sgViajero.getSgSolicitudViaje().getGerenciaResponsable().getId());
            correoGerencia = getResponsableByGerencia(sgViajero.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail();
            UtilLog4j.log.info(this, "gerencia responsable " + res.getNombre());
        }

        UtilLog4j.log.info(this, "Correo Gerencia: " + correoGerencia);
        UtilLog4j.log.info(this, "Correo Copia: " + correoCopia);

        sgViaje = guardar(sgOficina, res, opcionSeleccionada, usuario, idRuta, fechaSalida, fechaRegreso, horaSalida, horaRegreso, redondo);

        //Envío de e-mail por Solicitud de Viaje (el ciclo para esto viene dado por un for en el ViajeBeanModel)
        List<ViajeroVO> listaTemp = new ArrayList<ViajeroVO>();
        listaTemp.add(viajero);

        if (v) {
            create(sgViaje);
            UtilLog4j.log.info(this, "sgViaje id: " + sgViaje.getId());
        }
        return sgViaje;
    }

    private SgViaje guardar(int sgOficina, Usuario responsable, String opcionSeleccionada, Usuario usuario, int idRuta, Date fechaSalida,
            Date fechaRegreso, Date horaSalida, Date horaRegreso, String redondo) {
        UtilLog4j.log.info(this, "guardar viaje");
        SgViaje sgViaje = new SgViaje();
        try {
            sgViaje.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_POR_SALIR));
            sgViaje.setResponsable(responsable);
            sgViaje.setGenero(usuario);
            sgViaje.setFechaGenero(new Date());
            sgViaje.setHoraGenero(new Date());
            sgViaje.setEliminado(Constantes.NO_ELIMINADO);
            sgViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(idRuta));
            sgViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(2));
            sgViaje.setFechaProgramada(fechaSalida);
            sgViaje.setFechaRegreso(fechaRegreso);
            sgViaje.setHoraProgramada(horaSalida);
            sgViaje.setHoraRegreso(horaRegreso);

            sgViaje.setSgOficina(sgOficinaRemote.find(sgOficina));
            if (opcionSeleccionada.equals("a")) {
                sgViaje.setAutobus(Constantes.BOOLEAN_TRUE);
                sgViaje.setVehiculoPropio(Constantes.BOOLEAN_FALSE);
                sgViaje.setVehiculoAsignadoEmpresa(Constantes.BOOLEAN_FALSE);
            } else if (opcionSeleccionada.equals("p")) {
                sgViaje.setAutobus(Constantes.BOOLEAN_FALSE);
                sgViaje.setVehiculoPropio(Constantes.BOOLEAN_TRUE);
                sgViaje.setVehiculoAsignadoEmpresa(Constantes.BOOLEAN_FALSE);
            } else {
                sgViaje.setAutobus(Constantes.BOOLEAN_FALSE);
                sgViaje.setVehiculoPropio(Constantes.BOOLEAN_FALSE);
                sgViaje.setVehiculoAsignadoEmpresa(Constantes.BOOLEAN_TRUE);
            }
            sgViaje.setRedondo(redondo.equals(Constantes.redondo) ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            sgViaje.setCodigo("VI" + getDigitosAño(new Date()) + "-" + String.valueOf(folioRemote.getFolio(Constantes.CONSECUTIVO_VIAJE)));
            UtilLog4j.log.info(this, "3");

            //Prepara el viaje enviar el correo
            UtilLog4j.log.info(this, "guardó el viaje");

        } catch (Exception e) {
            UtilLog4j.log.info(this, "e: guardar  appendQuery(appendQuery(appendQuery(" + e.getMessage());
        }
        return sgViaje;
    }

    private boolean actualizaSolicitud(List<ViajeroVO> listaViajero, List<SgSolicitudViaje> lsol, Usuario usuario) {
        boolean va  = true;
        List<SgViajero> lvi;
        TreeSet<String> codigo = new TreeSet<String>();
        for (ViajeroVO v : listaViajero) {
            codigo.add(v.getCodigoSolicitudViaje());
        }
        for (SgSolicitudViaje sol : lsol) {
            try {
                UtilLog4j.log.info(this, "1");
                UtilLog4j.log.info(this, "2");
                UtilLog4j.log.info(this, "Codigo sol: " + sol.getCodigo());
                lvi = sgViajeroRemote.getViajerosBySolicitudViajeList(sol, false);
                UtilLog4j.log.info(this, "3");
                for (SgViajero sgViajero : lvi) {
                    if (sgViajero.getSgViaje() == null) {
                        UtilLog4j.log.info(this, "4");
                        va  = false;
                        break;
                    }
                }
                if (va) {
                    UtilLog4j.log.info(this, "5");
                    sgEstatusAprobacionRemote.finalizeRequest(sol.getId(), usuario, 0);
                }
            } catch (SIAException ex) {
                Logger.getLogger(SgViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return va;
    }

    /*
     * recupera los viajes en generados y que no ha llegado la fecha de salida
     */
    public List<ViajeVO> getRoadTripByExit(
            int idOficina, int idEstatus, int idEstatus2,
            boolean conViajeros, Date fecha1, Date fecha2,
            boolean detRuta, int tipoSolicitud, boolean enInterseccion,
            Boolean conChofer) throws SIAException {
        UtilLog4j.log.info(this, "SgViajeImpl.getRoadTripByExit()");

        List<ViajeVO> list = new ArrayList<ViajeVO>();
        clearQuery();
        try {
            QuerysBaseViajes();
            appendQuery(queryBaseViajesSalida);
            appendQuery(" WHERE v.estatus in (").append(idEstatus).append(", ").append(idEstatus2).append(")");
            appendQuery(" and  v.sg_oficina =   ").append(idOficina);
            appendQuery(" AND v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery(" AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            if (fecha1 != null && fecha2 != null) {
                appendQuery(" and v.fecha_programada >=  '").append(Constantes.FMT_yyyy_MM_dd.format(fecha1)).append(
                        "'  and v.fecha_programada <= '").append(Constantes.FMT_yyyy_MM_dd.format(fecha2)).append("' ");
            }

            if (Constantes.SOLICITUDES_TERRESTRE_OFICINA == tipoSolicitud) {
                appendQuery(" and drt.ID is not null ");
            } else if (Constantes.SOLICITUDES_TERRESTRE_CIUDAD == tipoSolicitud) {
                appendQuery(" and drc.ID is not null ");
            }

            if (!enInterseccion) {
                appendQuery(" and v.ID not in (select SG_VIAJE_A from GR_INTERSECCION where ELIMINADO = 'False')"
                        + "and v.ID not in (select SG_VIAJE_B from GR_INTERSECCION where ELIMINADO = 'False')");
            }

            if (conChofer != null && conChofer) {
                appendQuery(" AND v.CONCHOFER = '").append(Constantes.BOOLEAN_TRUE).append("' ");
            } else if (conChofer != null && !conChofer) {
                appendQuery(" AND v.CONCHOFER = '").append(Constantes.BOOLEAN_FALSE).append("' ");
            }

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                list.add(castViajePorSalir(objects, conViajeros, detRuta));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return list;
    }

    /**
     *
     * appendQuery("SELECT v.id, v.codigo, e.id, e.nombre, v.autobus,
     * v.vehiculo_propio, v.vehiculo_asignado_empresa, v.responsable,
     * v.fecha_salida, v.hora_salida, "); appendQuery(" v.fecha_regreso,
     * v.hora_regreso, v.sg_oficina, v.sg_viaje, "); appendQuery("
     * v.SG_VIAJE_CIUDAD, rt.id, rt.nombre, v.sg_oficina, v.si_adjunto, ");
     * appendQuery(" u.nombre, v.redondo, v.fecha_programada, "); appendQuery("
     * v.hora_programada, a.uuid ");
     */
    private ViajeVO castViajePorSalir(Object[] objects, boolean conViajeros, boolean detRuta) {
        ViajeVO vo;
        vo = new ViajeVO();
        try {
            vo.setId((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setIdEstatus((Integer) objects[2]);
            vo.setStatus((String) objects[3]);
            vo.setAutobus((Boolean) objects[4]);
            vo.setVehiculoPropio((Boolean) objects[5]);
            vo.setVehiculoEmpresa((Boolean) objects[6]);
            vo.setIdUsuario((String) objects[7]);
            vo.setFechaSalida((Date) objects[8]);
            vo.setHoraSalida((Date) objects[9]);
            vo.setFechaRegreso((Date) objects[10]);
            vo.setHoraRegreso((Date) objects[11]);
            vo.setIdOficinaOrigen((Integer) objects[12]);
            vo.setSgViaje(objects[13] != null ? (Integer) objects[13] : 0);
            vo.setIdSgViajeCiudad(objects[14] != null ? (Integer) objects[14] : 0);
            vo.setIdRuta(objects[15] != null ? (Integer) objects[15] : 0);
            vo.setRuta((String) objects[16]);
            vo.setIdOficinaDestino(objects[17] != null ? (Integer) objects[17] : 0);
            vo.setIdAdjunto(objects[18] != null ? (Integer) objects[18] : 0);
            vo.setResponsable((String) objects[19]);
            vo.setRedondo((Boolean) objects[20]);
            vo.setFechaProgramada((Date) objects[21]);
            vo.setHoraProgramada((Date) objects[22]);
            vo.setUuid(objects[23] != null ? (String) objects[23] : "");
            vo.getVehiculoVO().setCapacidadPasajeros(objects[24] != null ? (Integer) objects[24] : 0);
            vo.setDestino((String) objects[25]);
            vo.setTiempoViaje((String) objects[26]);
            vo.setConInter((Boolean) objects[27]);
            vo.setOrigen((String) objects[28]);
            vo.setResponsableTel(objects[29] != null ? (String) objects[29] : "");
            vo.setConChofer((Boolean) objects[30]);
            if (vo.getFechaSalida() != null && vo.getHoraSalida() != null) {
                vo.setTiempoViajeRealVal();
            }
            if (conViajeros) {
                vo.setListaViajeros(sgViajeroRemote.getTravellersByTravel(vo.getId(), null));
            }
            if (detRuta) {
                vo.setLstRutaDet(this.getRutaSectores(vo.getIdRuta()));
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return vo;

    }

    public List<ViajeVO> getSgViajeBySgOficinaAndEstatus(int idSgOficina, int idEstatus) {
        UtilLog4j.log.info(this, "SgViajeImpl.getSgViajeBySgOficinaAndEstatus()");

        clearQuery();

        List<ViajeVO> list = new ArrayList<ViajeVO>();

        appendQuery("SELECT ");
        appendQuery("v.ID, "); //0
        appendQuery("v.CODIGO, "); //1
        appendQuery("v.ESTATUS AS ID_ESTATUS, "); //2
        appendQuery("(SELECT nombre FROM ESTATUS WHERE ID=v.ESTATUS) AS NOMBRE_ESTATUS, "); //3
        appendQuery("v.AUTOBUS, "); //4
        appendQuery("v.VEHICULO_PROPIO, "); //5
        appendQuery("v.VEHICULO_ASIGNADO_EMPRESA, "); //6
        appendQuery("v.RESPONSABLE, "); //7
        appendQuery("v.FECHA_SALIDA, "); //8
        appendQuery("v.HORA_SALIDA, "); //9
        appendQuery("v.FECHA_REGRESO,"); //10
        appendQuery("v.HORA_REGRESO, "); //11
        appendQuery("v.SG_OFICINA AS ID_SG_OFICINA_ORIGEN, "); //12
        appendQuery("CASE WHEN v.SG_OFICINA IS null THEN '' "); //13
        appendQuery("     WHEN v.SG_OFICINA IS NOT null THEN (SELECT o.NOMBRE FROM SG_OFICINA o WHERE o.ID=v.SG_OFICINA) ");
        appendQuery("END AS nombre_origen_sg_oficina, ");
        appendQuery("CASE WHEN v.SG_VIAJE IS null THEN -1 "); //14
        appendQuery("     WHEN v.SG_VIAJE IS NOT null THEN v.SG_VIAJE ");
        appendQuery("END AS id_sg_viaje_ida, ");
        appendQuery("CASE WHEN v.SG_VIAJE_CIUDAD IS null THEN -1  "); //15
        appendQuery("     WHEN v.SG_VIAJE_CIUDAD IS NOT null THEN v.SG_VIAJE_CIUDAD ");
        appendQuery("END AS id_sg_viaje_ciudad, ");
        appendQuery("CASE WHEN v.SG_VIAJE_CIUDAD IS null THEN '' "); //16
        appendQuery("     WHEN v.SG_VIAJE_CIUDAD IS NOT null THEN (SELECT nombre FROM SI_CIUDAD c WHERE c.ID=(SELECT vc.SI_CIUDAD FROM SG_VIAJE_CIUDAD vc WHERE vc.ID=v.SG_VIAJE_CIUDAD)) ");
        appendQuery("END AS nombre_sg_viaje_ciudad, ");
        appendQuery("CASE WHEN v.SG_RUTA_TERRESTRE IS null THEN -1  "); //17
        appendQuery("     WHEN v.SG_RUTA_TERRESTRE IS NOT null THEN (SELECT r.ID FROM SG_RUTA_TERRESTRE r WHERE r.ID=v.SG_RUTA_TERRESTRE) ");
        appendQuery("END AS id_sg_ruta_terrestre, ");
        appendQuery("CASE WHEN v.SG_RUTA_TERRESTRE IS null THEN ''  "); //18
        appendQuery("     WHEN v.SG_RUTA_TERRESTRE IS NOT null THEN (SELECT r.NOMBRE FROM SG_RUTA_TERRESTRE r WHERE r.ID=v.SG_RUTA_TERRESTRE) ");
        appendQuery("END AS nombre_sg_ruta_terrestre, ");
        appendQuery(" CASE WHEN v.SG_viaje_ciudad IS null THEN (SELECT drt.SG_OFICINA FROM SG_DETALLE_RUTA_TERRESTRE drt  WHERE drt.SG_RUTA=v.SG_RUTA_TERRESTRE  AND drt.DESTINO='True' AND drt.ELIMINADO='False') ");
        appendQuery("   else (select vc.id from sg_viaje_ciudad vc, si_ciudad ci where vc.id = v.sg_viaje_ciudad  and vc.si_ciudad = ci.id and vc.eliminado = 'False') ");
        appendQuery("   END AS id_sg_oficina_destino, ");
        appendQuery(" CASE WHEN v.SG_viaje_ciudad IS not null THEN (SELECT c.NOMBRE FROM SI_CIUDAD c WHERE c.ID=(SELECT vc.SI_CIUDAD FROM SG_VIAJE_CIUDAD vc WHERE vc.ID=v.SG_VIAJE_CIUDAD and eliminado = 'False')) "); //20
        appendQuery("     WHEN v.SG_viaje_ciudad IS  null THEN (SELECT o.nombre FROM SG_OFICINA o WHERE o.ID=(SELECT drt.SG_OFICINA FROM SG_DETALLE_RUTA_TERRESTRE drt WHERE drt.SG_RUTA=v.SG_RUTA_TERRESTRE AND drt.DESTINO='True' AND drt.ELIMINADO='False')) ");
        appendQuery("END AS destino, ");
        appendQuery("CASE WHEN v.SI_ADJUNTO IS null THEN -1  "); //21
        appendQuery("     WHEN v.SI_ADJUNTO IS NOT null THEN v.SI_ADJUNTO ");
        appendQuery("END AS id_si_adjunto, ");
        appendQuery("(SELECT u.NOMBRE FROM USUARIO u WHERE u.ID=v.RESPONSABLE) AS nombre_responsable, "); //22
        appendQuery("  v.redondo,"); //23
        appendQuery("  v.fecha_programada, "); //24
        appendQuery("  v.hora_programada, "); //25

        appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
        appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
        appendQuery(" END AS uuid_adjunto "); //26

        appendQuery(" FROM SG_VIAJE v  ");
        appendQuery(" WHERE v.ESTATUS=").append(idEstatus).append(" ");
        if (idSgOficina != 0) {
            appendQuery("AND v.SG_OFICINA=").append(idSgOficina).append(" ");
        }
        appendQuery(" AND v.SG_TIPO_ESPECIFICO=2 ");
        appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

//        UtilLog4j.log.info(this, "WQ: " + query.toString());
        List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();

        ViajeVO vo;

        for (Object[] objects : result) {
            vo = new ViajeVO();
            vo.setId((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setIdEstatus((Integer) objects[2]);
            vo.setStatus((String) objects[3]);
            vo.setAutobus((Boolean) objects[4]);
            vo.setVehiculoPropio((Boolean) objects[5]);
            vo.setVehiculoEmpresa((Boolean) objects[6]);
            vo.setIdUsuario((String) objects[7]);
            vo.setFechaSalida((Date) objects[8]);
            vo.setHoraSalida((Date) objects[9]);
            vo.setFechaRegreso((Date) objects[10]);
            vo.setHoraRegreso((Date) objects[11]);
            vo.setIdOficinaOrigen((Integer) objects[12]);
            vo.setOficina((String) objects[13]);
            vo.setSgViaje((Integer) objects[14]);
            vo.setIdSgViajeCiudad((Integer) objects[15]);
            vo.setDestinoCiudad((String) objects[16]);
            vo.setIdRuta((Integer) objects[17]);
            vo.setRuta((String) objects[18]);
            vo.setIdOficinaDestino(objects[19] != null ? (Integer) objects[19] : 0);
            vo.setDestinoRuta((String) objects[20]);
            vo.setIdAdjunto((Integer) objects[21]);
            vo.setResponsable((String) objects[22]);
            vo.setRedondo((Boolean) objects[23]);
            vo.setFechaProgramada((Date) objects[24]);
            vo.setHoraProgramada((Date) objects[25]);
            vo.setUuid((String) objects[26]);
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list != null && !list.isEmpty() ? list.size() : "0") + " Viajes en Estatus: " + idEstatus);

        return list;
    }

    private ViajeVO castViajeVO(Object[] objects) {
        ViajeVO vo = new ViajeVO();
        try {
            vo = new ViajeVO();
            vo.setId((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setStatus((String) objects[2]);
            vo.setFechaSalida((Date) objects[3]);
            vo.setHoraSalida((Date) objects[4]);
            vo.setFechaRegreso((Date) objects[5]);
            vo.setHoraRegreso((Date) objects[6]);
            vo.setResponsable((String) objects[7]);
            vo.setTerrestre((String) objects[8]);
            vo.setAutobus((Boolean) objects[9]);
            vo.setVehiculoPropio((Boolean) objects[10]);
            vo.setVehiculoEmpresa((Boolean) objects[11]);
            vo.setOrigen((String) objects[12]);
            vo.setIdRuta((Integer) objects[13]);
            vo.setRuta((String) objects[14]);
            vo.setSgViaje((Integer) objects[15]);
            vo.setSiAdjunto((Integer) objects[16]);
            vo.setRedondo((Boolean) objects[17]);
            vo.setFechaProgramada((Date) objects[18]);
            vo.setHoraProgramada((Date) objects[19]);
            vo.setIdOficinaOrigen((Integer) objects[20]);
            vo.setEstatusAnterior((Integer) objects[21]);
            vo.setUuid((String) objects[22]);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al hacer el casting de Viaje");
            UtilLog4j.log.fatal(e);
        }
        return vo;
    }

    public int tolalViajeEnProcesso(int estatus, String idUsuarioGenero, int oficina) {
        try {
            clearQuery();
            query.append("SELECT count(distinct v.id) FROM SG_VIAJE v");
            query.append(" WHERE v.estatus = ").append(estatus);
            query.append(" and v.sg_oficina = ").append(oficina);
            //query.append(" AND v.genero = '").append(idUsuarioGenero).append("'");
            query.append(" AND v.redondo = '").append(Constantes.BOOLEAN_TRUE).append("'");
            query.append(" AND v.sg_viaje is null");
            query.append(" AND v.SG_TIPO_ESPECIFICO = 2");
            query.append(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            UtilLog4j.log.info(this, "Recupera los viajes en proceso " + query.toString());
            return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
        } catch (Exception e) {
            e.getStackTrace();
            return 0;
        }

    }

    /*
     * recupera los viajes terrestres en generados y que estan en proceso
     */
    public List<ViajeVO> getRoadTripInProcess(int estatus, String idUsuarioGenero, int oficina) {
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            appendQuery("SELECT distinct v.id, v.codigo, e.nombre,  v.fecha_salida,  v.hora_salida, "); //4
            appendQuery("v.fecha_regreso, v.hora_regreso, u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo,  v.autobus,  v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa, o.nombre, rt.id, rt.nombre,"); //14
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje "); //16
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //17
            appendQuery("    end, ");
            appendQuery("  v.redondo, v.fecha_programada,  v.hora_programada,  o.id, ");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");
            //
            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            //
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
            appendQuery(" sg_ruta_terrestre rt");
            appendQuery(" WHERE v.estatus = ").append(estatus);

//	    appendQuery(" AND v.genero = '").append(idUsuarioGenero).append("'");
            appendQuery(" AND v.sg_oficina = ").append(oficina);
            appendQuery(" AND v.sg_viaje is  null");
            appendQuery(" AND v.estatus = e.id");
            appendQuery("  AND v.redondo = '").append(Constantes.BOOLEAN_TRUE).append("'");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND te.id = 2");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_ruta_terrestre = rt.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            UtilLog4j.log.info(this, "Recupera los viajes en proceso " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                lv.add(castViajeVO(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error en recuperar los viajes en proceso #  # # # # # # # # " + e.getMessage());
        }
        return lv;
    }

    /*
     * recupera el total de viajes cuyo destino es la oficina actual salida
     */
    public List<ViajeVO> getRoadTripDesOffice(int sgOficina, int estatus, String idUsuarioRegresa, boolean traerRetorno) {
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            appendQuery("SELECT distinct  v.id,"); //0
            appendQuery("v.codigo, ");//1
            appendQuery("e.nombre, "); //2
            appendQuery("v.fecha_salida, "); //3
            appendQuery("v.hora_salida, "); //4
            appendQuery("v.fecha_regreso,"); //5
            appendQuery("v.hora_regreso,"); //6
            appendQuery("u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa,"); //11
            appendQuery("o.nombre,"); //12
            appendQuery("rt.id,"); //13
            appendQuery("rt.nombre,"); //14
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje ");//16
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //17
            appendQuery("    end, ");
            appendQuery("  v.redondo,");
            appendQuery("  v.fecha_programada, ");
            appendQuery("  v.hora_programada, ");
            appendQuery("  o.id,");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");
            //
            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            //
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
            appendQuery(" sg_ruta_terrestre rt, SG_DETALLE_RUTA_CIUDAD drc, sg_detalle_ruta_terrestre drt");
            appendQuery(" WHERE (v.SG_VIAJE is not null or drt.DESTINO = 'True' ").append(")");
            appendQuery(" AND v.estatus = ").append(estatus);
            // appendQuery(" AND v.usuario_regresa_viaje = '").append(idUsuarioRegresa).append("'");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.sg_tipo_especifico = 2");
            appendQuery(" AND v.sg_ruta_terrestre = rt.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND rt.id = drt.sg_ruta");
            appendQuery(" AND drt.sg_oficina = ").append(sgOficina);
            appendQuery(" AND v.redondo = '").append(Constantes.BOOLEAN_TRUE).append("'");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND rt.sg_oficina = o.id");
            appendQuery(" AND (rt.id = drc.SG_RUTA_TERRESTRE  or rt.id = drt.SG_RUTA )");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND drt.eliminado = 'False'");
            UtilLog4j.log.info(this, "Q: viajes por oficina des : " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                ViajeVO viajeVo = castViajeVO(objects);
                if (traerRetorno) {
                    viajeVo.setTieneRegresoValor(this.tieneRegreso(viajeVo.getId()));
                }
                lv.add(viajeVo);
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exc: " + e.getMessage());
//            throw new SIAException("Ocurrio un error al recuperar las rutas de las oficinas. ");
        }
        return lv;
    }

    /*
     * Trae todos los registros de viaje de regreso para finalizar
     */
    public List<ViajeVO> getTravellersByFinalize(int estatus, String idUsuarioGenero, int oficina) {
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            appendQuery("SELECT distinct  v.id,");
            appendQuery(" v.codigo, ");
            appendQuery(" e.nombre, ");
            appendQuery(" v.fecha_salida,");
            appendQuery(" v.hora_salida, ");
            appendQuery(" v.fecha_regreso,");
            appendQuery(" v.hora_regreso,");
            appendQuery(" u.nombre as responsable, ");
            appendQuery(" te.nombre as tipo, ");
            appendQuery(" v.autobus, ");
            appendQuery(" v.vehiculo_propio, ");
            appendQuery(" v.vehiculo_asignado_empresa,");
            appendQuery(" o.nombre,");
            appendQuery(" rt.id,");
            appendQuery(" rt.nombre,");
            //    appendQuery(" 'True',");
            appendQuery(" case when v.sg_viaje is null then 0");
            appendQuery("      when v.sg_viaje is not null then v.sg_viaje ");
            appendQuery("     end as idViaje,");
            appendQuery(" case when v.si_adjunto is null then 0");
            appendQuery("      when v.si_adjunto is not null then v.si_adjunto ");
            appendQuery("     end as adjunto, ");
            appendQuery("  v.redondo,");
            appendQuery("  v.fecha_programada, ");
            appendQuery("  v.hora_programada, ");
            appendQuery("  o.id, ");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");
            //
            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            //
            appendQuery("  FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
            appendQuery("  sg_ruta_terrestre rt");
            appendQuery(" WHERE v.redondo = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND v.estatus = ").append(estatus);
            appendQuery(" and v.sg_oficina = ").append(oficina);
            //appendQuery(" AND v.usuario_regresa_viaje = '").append(idUsuarioGenero).append("'");
            appendQuery("  AND v.sg_tipo_especifico = 2");
            appendQuery("  AND v.estatus = e.id ");
            appendQuery("  AND v.responsable = u.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery("  AND v.sg_ruta_terrestre = rt.id");
            appendQuery("  AND rt.sg_oficina = o.id");
            appendQuery("  AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");

            UtilLog4j.log.info(this, "Recupera las oficinas de los viajes: " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                lv.add(castViajeVO(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exc: " + e.getMessage());
//            throw new SIAException("Ocurrio un error al recuperar las rutas de las oficinas. ");
        }
        return lv;
    }

    public int getCountTrip(int idOficina, int estatus, String idUsuarioGenero) throws SIAException {
        try {
            clearQuery();
            appendQuery("SELECT count(distinct v.id)");
            appendQuery(" FROM SG_VIAJE v");
            appendQuery(" WHERE v.estatus IN (   ").append(estatus).append(", ").append(Constantes.ESTATUS_VIAJE_CREADO).append(")");
            //appendQuery(" and v.genero = '").append(idUsuarioGenero).append("'");
            appendQuery(" AND v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery(" AND v.sg_oficina =  ").append(idOficina);
            //      appendQuery(" AND v.id NOT IN (SELECT m.sg_viaje  FROM sg_viaje_si_movimiento m)");
            appendQuery(" AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());
            return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
        } catch (Exception e) {
            throw new SIAException("Error en algun lado" + e.getMessage());

        }
    }

    public int getCountTripOffice(int idOficina, int estatus, String idSesion) throws SIAException {
        try {
            clearQuery();
            query.append("select count(v.ID) from SG_VIAJE v ");
            query.append("      inner join SG_RUTA_TERRESTRE r on v.SG_RUTA_TERRESTRE = r.ID");
            query.append("      inner join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.ID");
            query.append("  where v.ESTATUS = ").append(estatus);
            query.append("  and v.REDONDO = '").append(Constantes.BOOLEAN_TRUE).append("'");
            query.append("  and v.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  and drt.SG_OFICINA = ").append(idOficina);
            query.append("  AND v.sg_tipo_especifico = 2");
            UtilLog4j.log.info(this, "Q: total viajes por oficina des : " + query.toString());
            return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "e: " + e.getMessage());
            throw new SIAException("Error en algun lado");

        }
    }

    public int getCountTravelsByFinalize(int estatus, String idSesion, int oficina) throws SIAException {
        try {
            clearQuery();
            appendQuery("SELECT count(distinct  v.id) FROM SG_VIAJE v");
            appendQuery(" WHERE v.redondo = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND v.estatus = ").append(estatus);
            appendQuery("  AND v.sg_oficina = ").append(oficina);
            appendQuery("  AND v.sg_tipo_especifico = ").append(Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE);
            appendQuery("  AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");

            return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "e: " + e.getMessage());
            throw new SIAException("Error en algun lado");
        }
    }

    public boolean buscarRutaUsada(int idRuta) {
        boolean v = false;
        clearQuery();
        appendQuery("SELECT count(v.id)");
        appendQuery(" FROM SG_VIAJE v");
        appendQuery(" WHERE v.ELIMINADO='False' and v.SG_RUTA_TERRESTRE = ").append(idRuta);
        appendQuery(" AND v.ESTATUS NOT IN (").append(Constantes.ESTATUS_VIAJE_CANCELADO).append(", ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(")");
        int contador = ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
        if (contador > 0) {
            v = true;
        }
        return v;
    }

    public List<SgViaje> getTravelByResponsible(String idUsuario) {
        Query q = em.createNativeQuery("select * from sg_viaje v where v.estatus between 501 and 520 and v.responsable = '" + idUsuario + "' ", SgViaje.class);
        return q.getResultList();
    }

    public List<ViajeVO> getAirTravelByOffice(int idOficina, int status, String idUsuarioGenero) throws SIAException {
        try {
            clearQuery();
            QuerysBaseViajes();
            appendQuery(queryBaseViajesSalidaAereos);
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND v.sg_oficina = ").append(idOficina);
            appendQuery(" AND v.genero = '").append(idUsuarioGenero).append("'");
            appendQuery(" AND te.id = ").append(Constantes.SOLICITUDES_AEREA);
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.id NOT IN (SELECT m.sg_viaje FROM sg_viaje_si_movimiento m)");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

//            UtilLog4j.log.info(this, "Recupera los viajes aereos Estatus: " + status + " --> " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<ViajeVO> lv = new ArrayList<ViajeVO>();
            UtilLog4j.log.info(this, "Lo: " + lo.size());
            for (Object[] objects : lo) {
                lv.add(castViajeVO(objects));
            }

            return lv;
        } catch (Exception e) {
            throw new SIAException("Error en algun lado");
        }
    }

    public boolean addFile(SgViaje sgViaje, Usuario usuario, SiAdjunto siAdjunto) {
        boolean v;
        try {
            sgViaje.setSiAdjunto(siAdjunto);
            sgViaje.setModifico(usuario);
            sgViaje.setFechaModifico(new Date());
            sgViaje.setHoraModifico(new Date());
            edit(sgViaje);
            v = true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al agregar el archivo ." + e.getMessage());
            v = false;
        }
        return v;
    }

    public void update(SgViaje sgViaje, Usuario usuario) {
        try {
            sgViaje.setSiAdjunto(null);
            sgViaje.setModifico(usuario);
            sgViaje.setFechaModifico(new Date());
            sgViaje.setHoraModifico(new Date());
            edit(sgViaje);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al agregar el archivo ." + e.getMessage());
            UtilLog4j.log.error(e);
        }
    }

    /**
     * Creo: NLopez
     *
     * @param idViaje
     * @param usuario
     */
    public void updateViajeDireccion(Integer idViaje, Usuario usuario) {
        try {
            SgViaje sgViaje = sgViajeRemote.find(idViaje);
            Estatus estatus = sgViaje.getEstatus();
            sgViaje.setEstatus(sgViaje.getEstatusAnterior());
            sgViaje.setEstatusAnterior(estatus);

            boolean correoEnviado = false;

            StringBuilder correoPara = new StringBuilder();

            List<Integer> li = new ArrayList<Integer>();
            li.add(Constantes.SGL_RESPONSABLE);
            li.add(Constantes.SGL_SEGURIDAD);
            li.add(Constantes.ROL_CENTRO_OPERACION);
            List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);

            int nlist = lu.size();
            int x = 1;

            for (UsuarioRolVo usuario1 : lu) {
                correoPara.append(usuario1.getCorreo());
                if (x < nlist) {
                    correoPara.append(", ");
                }
                x++;
            }
            correoEnviado = notificacionViajeRemote.aprobarViajeDireccion(usuario.getNombre(), correoPara.toString(), idViaje, sgViaje.getCodigo());
            if (correoEnviado) {
                sgViaje.setSiAdjunto(null);
                sgViaje.setModifico(usuario);
                sgViaje.setFechaModifico(new Date());
                sgViaje.setHoraModifico(new Date());
                edit(sgViaje);
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al agregar el archivo ." + e.getMessage());
        }
    }

    public boolean updateTrips(Usuario usuario, SgViaje sgViaje, SgViajeVehiculo sgViajeVehiculo, VehiculoVO vehiculoVO,
            String opcionSeleccionada, int idRuta, String redondo, boolean intercepcion) {
        boolean v = false;
        String cp = "";
        String cc = "";
        try {
            sgViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(idRuta));
            sgViaje.setModifico(usuario);
            sgViaje.setFechaModifico(new Date());
            sgViaje.setHoraModifico(new Date());
            sgViaje.setRedondo(redondo.equals(Constantes.redondo) ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            sgViaje.setConIntercepcion(intercepcion ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            if (opcionSeleccionada.equals("s")) {
                sgViajeVehiculoRemote.update(usuario, sgViajeVehiculo, sgVehiculoRemote.find(vehiculoVO.getId()));
            }
            //Notifica cambio en viaje
            List<ViajeroVO> lvjro = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
            List<Integer> li = new ArrayList<Integer>();
            li.add(Constantes.SGL_RESPONSABLE);
            li.add(Constantes.SGL_ANALISTA);
            List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);
            edit(sgViaje);
            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al modificar el viaje + + + " + sgViaje.getCodigo() + " + + + + + + + + + + " + e.getMessage());
        }
        return v;
    }

    public void cancelTrip(Usuario usuario, SgViaje sgViaje, String motivo, boolean vieneCancelaViajero, SiMovimiento siMovimiento,
            boolean vieneSeguridad) throws SIAException {
        UtilLog4j.log.info(this, "SgViajeImpl.cancelTrip()");

        try {
            boolean v = false;
            if (sgViaje.getSgTipoEspecifico().getId() == 3 && !vieneCancelaViajero) {
                UtilLog4j.log.info(this, "Envío de mail cancelacion de viaje aereo");

            }
            //Obtiene la lista de notificaciones
            String correoCopia = usuario.getEmail();

            if (sgViaje.isVehiculoAsignadoEmpresa()) {
                UtilLog4j.log.info(this, "VehiculoAsignadoEmpresa");

                List<ViajeroVO> listaViajero = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
                List<SgSolicitudViaje> listSolViajeTemp = new ArrayList<SgSolicitudViaje>();
                List<SgViajero> listViajeroTemp = new ArrayList<SgViajero>();
                SgSolicitudViaje sgSolicitudViaje;
                TreeSet<String> treeSetSolicitud = new TreeSet<String>();

                //Solicitudes atendidas
                for (ViajeroVO vj : listaViajero) {
                    treeSetSolicitud.add(vj.getCodigoSolicitudViaje());
                    listViajeroTemp.add(sgViajeroRemote.find(vj.getId()));
                }
                UtilLog4j.log.info(this, "Lista viajero temporal envio de mail: " + listViajeroTemp.size());
                for (String codigo : treeSetSolicitud) {
                    sgSolicitudViaje = sgSolicitudViajeRemote.findByCode(codigo); //Agregamos la sol a la lista temporal de solicitudes
                    if (sgSolicitudViaje != null) {
                        listSolViajeTemp.add(sgSolicitudViaje);
                    }
                }
                UsuarioResponsableGerenciaVo urgv;
                SgViajeVehiculo sgViajeVehiculo = sgViajeVehiculoRemote.getVehicleByTravel(sgViaje.getId());
                UtilLog4j.log.info(this, "Lista solicitudes temporal envio de mail: " + listSolViajeTemp.size());
                if (!vieneCancelaViajero) { //Comprueba si la cancelacion del viaje viene del metodo viajero o desde el bean
                    //No es el usuario prueba
                    if (listSolViajeTemp != null && !listSolViajeTemp.isEmpty()) {
                        for (SgSolicitudViaje solicitudViaje : listSolViajeTemp) {
                            urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, solicitudViaje.getGerenciaResponsable().getId());
                            v = notificacionViajeRemote.sendMailCancelTripCompanyCar(usuario.getNombre(), urgv.getEmailUsuario(), correoCopia, urgv.getNombreUsuario(),
                                    listaViajero, sgViajeVehiculo.getSgVehiculo().getId(), sgViaje.getCodigo(), sgViaje.getFechaProgramada(),
                                    sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(),
                                    sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(), sgViaje.isRedondo(), motivo, sgViaje.getResponsable().getNombre(),
                                    sgViaje.getResponsable().getTelefono());
                        }
                    } else {
                        v = notificacionViajeRemote.sendMailCancelTripCompanyCar(usuario.getNombre(), usuario.getEmail(), correoCopia, sgViaje.getResponsable().getNombre(),
                                listaViajero, sgViajeVehiculo.getSgVehiculo().getId(), sgViaje.getCodigo(), sgViaje.getFechaProgramada(),
                                sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(),
                                sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(), sgViaje.isRedondo(), motivo, sgViaje.getResponsable().getNombre(),
                                sgViaje.getResponsable().getTelefono());

                    }
                }

            } else { //Vehiculo de la empresas
                v = notificacionViajeRemote.sendMailCancelTripNoCompanyCar(usuario.getNombre(), sgViaje.getResponsable().getEmail(), correoCopia(usuario.getEmail(), sgViaje), sgViaje, motivo);

            }

            UtilLog4j.log.info(
                    this, "v: " + v);
            UtilLog4j.log.info(
                    this, "viene de viajero " + vieneCancelaViajero);

            if (!vieneCancelaViajero) {
                //Guarda en la tabla motivo
                siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(3), usuario);
                UtilLog4j.log.info(this, "guardó movimiento");
            } else {
                v = true; //Si viene de viajero prende la variable 'v' para hacer los cambios
            }
            if (v) {
                UtilLog4j.log.info(this, "SiMovimiento: appendQuery(" + siMovimiento.getMotivo());
                sgViaje.setEstatus(estatusRemote.find(500));
                sgViaje.setModifico(usuario);
                sgViaje.setFechaModifico(new Date());
                sgViaje.setHoraModifico(new Date());
                edit(sgViaje);
                //Guarda en relacion
                sgViajeSiMovimientoRemote.guardarViajeCancelado(usuario, sgViaje, siMovimiento);

                cancelStatusTravelNews(sgViaje.getId(), motivo, usuario.getId());
                //Nitificar Cancelacion de viajer por parte de seguridad
                if (vieneSeguridad) {
                    notificacionViajeRemote.sendMailCancelTripSecurity(usuario.getNombre(), notificaCancelacionViaje(), sgViaje.getId(), sgViaje.getCodigo(), motivo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            throw new SIAException("Ocurrio en error al cancelar el viaje");
        }
    }

    /**
     * Creo: NLopez
     *
     * @param usuario
     * @param sgViaje
     * @param motivo
     * @param siMovimiento
     * @param vieneSeguridad
     * @throws SIAException
     */
    public void pausaViaje(Usuario usuario, SgViaje sgViaje, String motivo, SiMovimiento siMovimiento, boolean vieneSeguridad) throws SIAException {
        UtilLog4j.log.info(this, "SgViajeImpl.cancelTrip()");
        try {
            List<Integer> li = new ArrayList<Integer>();
            li.add(Constantes.SGL_RESPONSABLE);
            li.add(Constantes.SGL_SEGURIDAD);
            li.add(Constantes.ROL_CENTRO_OPERACION);
            List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);
            String cc = traerCorreo(lu);
            String ccp = sgViaje.getGenero().getEmail();

            //Notificar Cancelacion de viajer por parte de seguridad            
            boolean v = notificacionViajeRemote.sendMailPausarViajeSecuridad(usuario.getNombre(), notificaCancelacionViaje(), sgViaje.getId(), sgViaje.getCodigo(), motivo);
            if (v) {
                v = notificacionViajeRemote.enviaCorreoAnalistaViajeEnAutorizacion(cc, ccp, sgViaje.getId(), sgViaje.getCodigo(), motivo, usuario.getNombre());
            }
            if (v) {
                //Guarda en la tabla motivo
                siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(Constantes.ID_SI_OPERACION_CANCELAR), usuario);
                sgViaje.setEstatusAnterior(sgViaje.getEstatus());
                sgViaje.setEstatus(estatusRemote.find(Constantes.VIAJE_ESPERA_AUTORIZACION));
                sgViaje.setModifico(usuario);
                sgViaje.setFechaModifico(new Date());
                sgViaje.setHoraModifico(new Date());
                edit(sgViaje);
                //Guarda en relacion
                UtilLog4j.log.debug(this, "viaje: " + sgViaje.toString());
                sgViajeSiMovimientoRemote.guardarViajeCancelado(usuario, sgViaje, siMovimiento);
                cancelStatusTravelNews(sgViaje.getId(), motivo, usuario.getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            throw new SIAException("Ocurrio en error al cancelar el viaje");
        }
    }

    private String correoCopiaRol(int idRol, int idModulo, boolean principal) {
        List<UsuarioRolVo> lur = siUsuarioRolRemote.traerUsuarioPorRolModulo(idRol, idModulo, Constantes.AP_CAMPO_DEFAULT);
        String correo = "";
        for (UsuarioRolVo usuarioRolVo : lur) {
            if (correo.isEmpty()) {
                correo = usuarioRolVo.getCorreo();
            } else {
                correo += ',' + usuarioRolVo.getCorreo();
            }
        }
        return correo;
    }

    private String notificaCancelacionViaje() {
        UtilLog4j.log.info(this, "SgViajeImpl.notificaCancelacionViaje()");
        String correo = "";
        UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, Constantes.ID_GERENCIA_IHSA);
        UtilLog4j.log.info(this, "Responsable ihsa: " + usuarioResponsableGerenciaVo.getNombreUsuario());
        correo = usuarioResponsableGerenciaVo.getEmailUsuario();
        UtilLog4j.log.info(this, "Correo notifica cancel seguridad: " + correo);
        return correo;
    }

    /**
     * Modifico: NLopez 07/11/2013 El adquirir correo
     *
     * @param correoAnalista
     * @param object
     * @return
     */
    private String correoCopia(String correoAnalista, Object object) {
        SgViaje sgViaje = null;
        SolicitudViajeVO solicitudViaje = null;
        List<ViajeroVO> lv = null;
        if (object instanceof SgViaje) {
            sgViaje = (SgViaje) object;
        } else if (object instanceof SolicitudViajeVO) {
            solicitudViaje = (SolicitudViajeVO) object;
        }
        String correo = correoAnalista;

        correo += "," + correoUsuariosResponsables();

        if (sgViaje != null) {
            lv = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
        } else if (solicitudViaje != null) {
            try {
                lv = sgViajeroRemote.getAllViajerosList(solicitudViaje.getIdSolicitud());
            } catch (Exception ex) {
                UtilLog4j.log.info(this, "Viajeros por solicitud" + ex.getMessage());
            }
        }
        for (ViajeroVO objects : lv) {
            if (objects.getIdInvitado() != 0) {
                correo += "," + objects.getCorreo();
            }
        }
        UtilLog4j.log.info(this, "COrreo copia: " + correo);
        return correo;
    }

    public void exitTrip(Usuario usuario, SgViaje sgViaje, int status, List<ViajeroVO> viajeros, boolean vieneBean) throws SIAException {
        UtilLog4j.log.info(this, "exitTrip");
        String correoAnalistasenOficina = "";
        boolean emailSent = true;
        sgViaje.setFechaSalida(new Date());
        sgViaje.setHoraSalida(new Date());
        String telefono = sgViaje.getResponsable().getTelefono() != null ? sgViaje.getResponsable().getTelefono() : "--";
        //
        //
        try {
            String idUsuarioRegresaViaje = "";
            //enviar notificacion de salida de correo--.
            if (sgViaje.getSgTipoEspecifico().getId() == 2) {
                if (sgViaje.getSgViajeCiudad() == null && sgViaje.getSgViajeLugar() == null) { // viaje a oficina
                    if (sgViaje.isRedondo()) {// VIajes redondos
                        for (SgDetalleRutaTerrestreVo dt : sgDetalleRutaTerrestreRemote.getAllSgDetalleRutaTerrestreBySgRutaTerrestre(sgViaje.getSgRutaTerrestre().getId(), "nombre", true, false)) {
                            UtilLog4j.log.info(this, "detalle " + dt.getNombreSgOficina());
                            //for (SgOficinaAnalistaVo oa : sgOficinaAnalistaRemote.getAllSgOficinaAnalista(dt.getIdSgOficina(), "id", true, false)) {
                            SgOficinaAnalistaVo oa = sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(dt.getIdSgOficina());
                            UtilLog4j.log.info(this, "oficina analista " + oa.getNombreSgOficina());
                            if (oa.getIdSgOficina() != sgViaje.getSgOficina().getId()) {
                                if (correoAnalistasenOficina.equals("")) {
                                    correoAnalistasenOficina = correoAnalistasenOficina + oa.getEmailAnalista();
                                } else {
                                    correoAnalistasenOficina += "," + oa.getEmailAnalista();
                                }
                                //    }
                                //Oficina destino de la ruta
                                if (dt.isDestino()) {
                                    idUsuarioRegresaViaje = oa.getIdAnalista();
                                }
                            }
                        }
                    } else { // Viaje es sencillo
                        if (sgViaje.getSgViaje() == null) {
                            idUsuarioRegresaViaje = sgViaje.getGenero().getId();
                        } else if (sgViaje.getSgViaje() != null) {
                            idUsuarioRegresaViaje = sgViaje.getSgViaje().getGenero().getId();
                        }
                    }
                } else { //viaje a ciudad o a lugar
                    idUsuarioRegresaViaje = sgViaje.getGenero().getId();
                    emailSent = notificacionViajeRemote.sendMailNotificacionParaFinalizacionViaje(sgViaje.getCodigo(), sgViaje.getFechaProgramada(),
                            sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(),
                            sgViaje.getHoraRegreso(), sgViaje.isRedondo(), Constantes.RUTA_TIPO_CIUDAD, sgViaje.getSgOficina().getNombre(),
                            sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(), telefono,
                            sgViaje.getSgViaje() == null ? true : false, sgViaje.getGenero().getEmail(), usuario.getId());
                }
                UtilLog4j.log.info(this, "correo para " + correoAnalistasenOficina);
            } else if (sgViaje.getSgTipoEspecifico().getId() == Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
                idUsuarioRegresaViaje = sgViaje.getGenero().getId();
            }

            if (emailSent) {
                // Notifica a centops
                if (vieneBean) {
                    List<ViajeroVO> listaV = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
                    notificacionViajeRemote.enviarCorreoCentopsSalidaViaje(sgViaje, telefono, listaV);
                } else {
                    if (viajeros != null && !viajeros.isEmpty()) {
                        List<ViajeroVO> listaTempViajaron = new ArrayList<>();
                        for (ViajeroVO viajero : viajeros) {
                            if (viajero.isViajo()) {
                                listaTempViajaron.add(viajero);
                                int inv = Constantes.CERO;
                                if (viajero.isAgregado()) {
                                    if (viajero.getTipoViajero() == Constantes.SG_TIPO_ESPECIFICO_INVITADO) {
                                        inv = sgInvitadoRemote.guardarInvitado(usuario.getId(), viajero.getInvitado(), viajero.getCorreo(), Constantes.EMPRESA_IHSA);
                                    }
                                    // agregar viajero a viaje//
                                    String motivo = "Viajero agregado al Viaje -" + sgViaje.getCodigo() + "- sin tener solicitud de viaje.";
                                    sgViajeroRemote.agregarViajeroAViaje(usuario.getId(), sgViaje.getId(), viajero.getIdUsuario(), viajero.getTipoViajero(), inv, usuario.getEmail(), motivo, Constantes.ID_SI_OPERACION_AGREGAR_VIAJERO);
                                }
                            } else if (!viajero.isViajo() && !viajero.isAgregado()) {
                                String viaj = viajero.getTipoViajero() == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO ? viajero.getUsuario() : viajero.getInvitado();
                                String motivo = "El viajero, " + viaj + ", no viajó en el viaje " + sgViaje.getCodigo() + ".";
                                sgViajeroSiMovimientoRemote.guardaMovimiento(usuario.getId(), viajero.getId(), motivo, Constantes.ID_SI_OPERACION_VIAJERO_NO_VIAJO);

                            }
                        }
                        notificacionViajeRemote.enviarCorreoCentopsSalidaViaje(sgViaje, telefono, listaTempViajaron);
                        //
                    } else {
                        notificacionViajeRemote.enviarCorreoCentopsSalidaViaje(sgViaje, telefono, viajeros);
                    }
                }
                sgViaje.setEstatus(estatusRemote.find(status));
                sgViaje.setUsuarioRegresaViaje(usuarioRemote.find(idUsuarioRegresaViaje));
                sgViaje.setModifico(usuario);
                sgViaje.setFechaModifico(new Date());
                sgViaje.setHoraModifico(new Date());
                edit(sgViaje);

                //publicar
                //Solo se crea la noticia cuando se crea el viaje
                //createEventNews(sgViaje.getId(), usuario.getId());
                correoAnalistasenOficina = "";
                //enviar una notificacion ala oficina de Origen para que finalize el viaje..

                if (sgViaje.getSgViaje() != null && sgViaje.getSgViaje().isEliminado()) {
                    //Extraer los correos de los analistas de la oficina origen del viaje..
                    //for (SgOficinaAnalista oa : this.sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(sgViaje.getSgOficina(), Constantes.BOOLEAN_FALSE)) {
                    for (SgOficinaAnalista oa : this.sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(sgViaje.getSgViaje().getSgOficina().getId(), Constantes.BOOLEAN_FALSE)) {
                        if (correoAnalistasenOficina.isEmpty()) {
                            correoAnalistasenOficina = correoAnalistasenOficina + oa.getAnalista().getEmail();
                        } else {
                            correoAnalistasenOficina += "," + oa.getAnalista().getEmail();
                        }
                    }
                    UtilLog4j.log.info(this, "Enviar correo para " + correoAnalistasenOficina);
                    if (sgViaje.getSgViajeCiudad() == null && sgViaje.getSgViajeLugar() == null) { // viaje a oficina
                        notificacionViajeRemote.sendMailNotificacionParaFinalizacionViaje(sgViaje.getCodigo(), sgViaje.getFechaProgramada(),
                                sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(),
                                sgViaje.getHoraRegreso(), sgViaje.isRedondo(), Constantes.RUTA_TIPO_CIUDAD, sgViaje.getSgOficina().getNombre(),
                                sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(), telefono,
                                sgViaje.getSgViaje() == null ? true : false, correoAnalistasenOficina, usuario.getId());

                    } else {
                        notificacionViajeRemote.sendMailNotificacionParaFinalizacionViaje(sgViaje.getCodigo(), sgViaje.getFechaProgramada(),
                                sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(),
                                sgViaje.getHoraRegreso(), sgViaje.isRedondo(), Constantes.RUTA_TIPO_OFICINA, sgViaje.getSgOficina().getNombre(),
                                sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(), telefono,
                                sgViaje.getSgViaje() == null ? true : false, correoAnalistasenOficina, usuario.getId());
                    }
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            throw new SIAException("Ocurrió un error al sacar el viaje " + e.getMessage());
        }
    }

    public List<ViajeVO> getAllRoadTripByExit(int estatus) {
        List<ViajeVO> lv = null;
        List<Object[]> l;
        ViajeVO o;
        try {
            clearQuery();
            appendQuery("SELECT v.id,v.codigo");
            appendQuery(" FROM  SG_VIAJE v ");
            appendQuery(" WHERE v.estatus = ").append(estatus);
            appendQuery(" AND  v.fecha_programada = cast('NOW' as date)");//
            appendQuery(" AND v.id NOT IN(SELECT vm.sg_viaje FROM SG_VIAJE_SI_MOVIMIENTO vm where vm.eliminado = 'False')"); //0

            UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());
            l = em.createNativeQuery(query.toString()).getResultList();
            UtilLog4j.log.info(this, "Size " + l.size());
            if (l != null && !l.isEmpty()) {
                lv = new ArrayList<ViajeVO>();
                for (Object[] objects : l) {
                    o = new ViajeVO();
                    o.setId((Integer) objects[0]);
                    lv.add(o);
                }
            }
            UtilLog4j.log.info(this, "Saliendo ");
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al traer los viajes por salir" + e.getMessage());
            return null;
        }
        return lv;
    }

    public List<ViajeVO> getCancelTrips(int idOficina, int status) {
        try {
            List<Object[]> lo;
            clearQuery();
            appendQuery("SELECT v.id,"); //0
            appendQuery("v.codigo, ");//1
            appendQuery("v.sg_oficina,"); //2
            appendQuery("e.nombre,"); //3

            appendQuery("v.fecha_salida, "); //4
            appendQuery("v.hora_salida, "); //5
            appendQuery("v.fecha_regreso,"); //6
            appendQuery("v.hora_regreso,"); //7
            appendQuery("u.nombre as responsable, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa"); //11
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND v.sg_oficina = ").append(idOficina);
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());
            lo = em.createNativeQuery(query.toString()).getResultList();
            List<ViajeVO> lv = new ArrayList<ViajeVO>();
            ViajeVO v;
            for (Object[] objects : lo) {
                v = new ViajeVO();
                v.setId((Integer) objects[0]);
                v.setCodigo(String.valueOf(objects[1]));
                int ofi = (Integer) objects[2];
                v.setOficina(sgOficinaRemote.find(ofi).getNombre());
                v.setStatus((String) objects[3]);
                v.setFechaSalida((Date) objects[4]);
                v.setHoraSalida((Date) objects[5]);
                v.setFechaRegreso((Date) objects[6]);
                v.setHoraRegreso((Date) objects[7]);
                v.setResponsable((String) objects[8]);
                v.setAutobus((Boolean) objects[9]);
                v.setVehiculoPropio((Boolean) objects[10]);
                v.setVehiculoEmpresa((Boolean) objects[11]);
                lv.add(v);
            }
            return lv;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * ************ BITACORA - CO_NOTICIA *********************************
     */
    private void createEventNewsByListViajero(Integer idViaje, String idUsuario, List<ViajeroVO> list, boolean isModificacion) {
        UtilLog4j.log.info(this, "createEventNewsByListViajero " + idViaje);
        String titulo;
        String mensaje = new String();
        String comodin = "";
        String mensajeAutomatico = "";
        CoNoticia noticia = null;
        //buscar viaje
        try {
            SgViaje sgViaje = find(idViaje);
            if (sgViaje.getCoNoticia() != null) {
                UtilLog4j.log.info(this, "modificar la noticia");
                mensajeAutomatico = "";
                // ya tiene noticia solo modificar por estatus
                //mensajeAutomatico = sgViaje.getCoNoticia().getMensajeAutomatico();
                noticia = sgViaje.getCoNoticia();
                mensaje = noticia.getMensajeAutomatico();
                if (!isModificacion) {
                    mensaje += mostrarEstatusAprobado(sgViaje.getEstatus().getNombre(), sgViaje.getModifico().getNombre(), sgViaje.getFechaModifico(), sgViaje.getHoraModifico());
                    noticia.setMensajeAutomatico(mensaje);
                } else {
                    UtilLog4j.log.info(this, "$$$$$$se modifico la noticia$$$$$$$$$$$");
                }
                noticia.setMensajeAutomatico(mensajeAutomatico);
                coNoticiaService.editNoticia(noticia, idUsuario);
                UtilLog4j.log.info(this, "Noticia modificada..");
            } else {
                //crea una noticia para el viaje
                UtilLog4j.log.info(this, "crear la noticia");
                titulo = "Viaje : ".concat(sgViaje.getCodigo());
                if (sgViaje.getSgTipoEspecifico().getId() == 2) {
                    //Es terrestre
                    mensaje = mostrarDetalleViajeTerrestre(sgViaje);
                } else {
                    //Es aereo
                    mensaje = mostrarDetalleViajeAereo(sgViaje);
                }
                //mensaje += "<p>Pendiente por salir el día " appendQuery((Constantes.FMT_TextDate.format(sgViaje.getFechaSalida())).concat(" ".concat(Constantes.FMT_hmm_a.format(sgViaje.getHoraSalida())).concat("</p>"));
                List<ComparteCon> listComparteCon = new ArrayList<ComparteCon>();
                ComparteCon comparteCon = null;
                //cast to ComparteCon
                if (list != null) {
                    for (ViajeroVO viajero : list) {
                        if (viajero.getIdInvitado() == 0) {
                            comparteCon = new ComparteCon(viajero.getIdUsuario(), viajero.getUsuario(), "", "Usuario");
                            listComparteCon.add(comparteCon);
                        }
                    }
                }
                //Agregar competentes del viaje
                //prueba
                if (idUsuario.equals("PRUEBA")) {
                    listComparteCon.add(new ComparteCon("PRUEBA", "", "", "Usuario"));
                } else {
                    //quien creo el viaje
                    //listComparteCon.add(new ComparteCon(idUsuario, "", "", "Usuario"));
                    //Responsables SGL //Seguridad

                    List<Integer> li = new ArrayList<Integer>();
                    li.add(Constantes.SGL_RESPONSABLE);
                    li.add(Constantes.SGL_SEGURIDAD);
                    li.add(Constantes.ROL_CENTRO_OPERACION);
                    List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);

                    for (UsuarioRolVo usuario1 : lu) {
                        listComparteCon.add(new ComparteCon(usuario1.getIdUsuario(), "", "", "Usuario"));
                    }

                    if (sgViaje.getSgViajeCiudad() != null) {
                        //Es un viaje a ua ciudad
                        //compartir con el analista de la ofna
                        List<SgOficinaAnalista> lis = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(sgViaje.getSgOficina().getId(), Constantes.BOOLEAN_FALSE);
                        for (SgOficinaAnalista analista : lis) {
                            listComparteCon.add(new ComparteCon(analista.getAnalista().getId(), "", "", "Usuario"));
                            UtilLog4j.log.info(this, "Compartido con " + analista.getAnalista().getNombre());
                        }
                    } else {
                        //es un viaje de oficina
                        //compartir con analistas de las ofinas por las que pasa..
                        //1. ir po las oficinas de la ruta que tiene el viaje
                        for (SgDetalleRutaTerrestre det : sgDetalleRutaTerrestreRemote.getDetailByRuote(idViaje, Constantes.BOOLEAN_FALSE)) {
                            //2. ir por los analistas del origen del viaje
                            for (SgOficinaAnalista ofAnalista : sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(det.getSgOficina().getId(), Constantes.BOOLEAN_FALSE)) {
                                listComparteCon.add(new ComparteCon(ofAnalista.getAnalista().getId(), "", "", "Usuario"));
                            }
                        }
                    }

                }

                if (listComparteCon != null || !listComparteCon.isEmpty()) {
                    CoNoticia noti = coNoticiaService.nuevaNoticia("SIA", titulo, "", mensaje, 0, 0, listComparteCon);

                    if (noti != null) {
                        //modificar el viaje
                        sgViaje.setCoNoticia(noti);
                        edit(sgViaje);
                        UtilLog4j.log.info(this, "Se publico la NOTICIA con la lista de viajeros");
                    }
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al crear el evento o la noticia por lista de viajeros" + e.getMessage());
        }
    }

    private String mostrarDetalleViajeAereo(SgViaje viaje) throws SIAException {
        StringBuilder detalle = new StringBuilder("");
        if (viaje != null) {
            detalle.append("<p>Se ha registrado un viaje Áereo de la ciudad de <strong>".concat(viaje.getSgItinerario().getSiCiudadOrigen().getNombre()).concat("</strong>"));
            detalle.append(" a la ciudad de <strong>".concat(viaje.getSgItinerario().getSiCiudadDestino().getNombre()).concat("</strong>"));
            detalle.append(" saliendo el dia <strong>".concat(viaje.getFechaSalida() != null ? Constantes.FMT_TextDate.format(viaje.getFechaSalida()) : Constantes.FMT_TextDateLarge.format(viaje.getFechaProgramada())).concat("</strong>"));
            detalle.append(", regresando el <strong>".concat(viaje.getFechaRegreso() != null ? Constantes.FMT_hmm_a.format(viaje.getFechaRegreso()) : "-").concat("</strong>"));
            detalle.append("</p>");
            detalle.append(mostrarEstatusAprobado("REGISTRADO", viaje.getGenero().getNombre(), viaje.getFechaGenero(), viaje.getHoraGenero()));
        }
        return detalle.toString();
    }

    /**
     * MLUIS 08/11/2013
     */
    private String mostrarDetalleViajeTerrestre(SgViaje viaje) throws SIAException {
        StringBuilder detalle = new StringBuilder("");
        String destino = "";
        if (viaje.getSgViajeCiudad() != null) {
            destino = viaje.getSgViajeCiudad().getSiCiudad().getNombre() + ", " + viaje.getSgViajeCiudad().getSiCiudad().getSiEstado().getNombre();
        } else if (viaje.getSgViajeLugar() != null) {
            destino = viaje.getSgViajeLugar().getSgLugar().getNombre();
        } else {
            for (SgDetalleRutaTerrestreVo d : sgDetalleRutaTerrestreRemote.getAllSgDetalleRutaTerrestreBySgRutaTerrestre(viaje.getSgRutaTerrestre().getId(), "id", true, false)) {
                if (d.isDestino()) {
                    destino = d.getNombreSgOficina();
                    break;
                }
            }
        }
        //StringBuilder fragmento = new StringBuilder()
        //es un viaje a una ciudad
        //ir a buscar ciudad
        detalle.append("<p>Se ha registrado un viaje Terrestre de la oficina <strong>".concat(viaje.getSgOficina().getNombre()).concat("</strong>"));
        detalle.append(" a <strong>").append(destino).append("</strong>");
        detalle.append(" saliendo el día <strong>".concat(Constantes.FMT_TextDate.format(viaje.getFechaProgramada())).concat("</strong>"));
        detalle.append(" <strong>".concat(Constantes.FMT_hmm_a.format(viaje.getHoraProgramada())).concat("</strong>"));
        detalle.append(", regresando el <strong>".concat(viaje.getFechaRegreso() != null ? Constantes.FMT_TextDate.format(viaje.getFechaRegreso()) : "-").concat("</strong>"));
        detalle.append(" <strong>".concat(viaje.getHoraRegreso() != null ? Constantes.FMT_hmm_a.format(viaje.getHoraRegreso()) : "-").concat(".</strong>"));
        detalle.append("</p>");
        detalle.append(mostrarEstatusAprobado("REGISTRÓ", usuarioRemote.find(viaje.getGenero().getId()).getNombre(), viaje.getFechaGenero(), viaje.getHoraGenero()));

        return detalle.toString();
    }
    //usado para las noticias

    private String mostrarEstatusAprobado(String estatusNombre, String usuarioRealizo, Date fecha, Date hora) {
        UtilLog4j.log.info(this, "mostrarEstatusAprobado");
        UtilLog4j.log.info(this, estatusNombre);
        UtilLog4j.log.info(this, usuarioRealizo);
        UtilLog4j.log.info(this, fecha.toString());
        UtilLog4j.log.info(this, hora.toString());
        StringBuilder estatusHtml = new StringBuilder("");

        estatusHtml.append("<table width=\"100%\" cellspacing=\"0\" border=\"0\" >");
        estatusHtml.append("<tr>");
        estatusHtml.append("<td width=\"20%\" align=\"left\" style=\" font:Arial, Helvetica, sans-serif; font-size:11px; font-weight: bold;color:gray;\">".concat(estatusNombre).concat("</td>"));
        estatusHtml.append("<td width=\"40%\" align=\"left\" style=\" font:Arial, Helvetica, sans-serif; font-size:11px;color:gray;\">".concat(usuarioRealizo).concat("</td>"));
        estatusHtml.append("<td width=\"40%\" align=\"left\" style=\" font:Arial, Helvetica, sans-serif; font-size:11px;color:gray;\">".concat(Constantes.FMT_TextDate.format(fecha)).concat(" ").concat(Constantes.FMT_hmm_a.format(hora)).concat("</td>"));
        estatusHtml.append("</tr>");
        estatusHtml.append("</table>");
        return estatusHtml.toString();
    }

    /**
     * Modifico: NLopez 07/11/2013 El adquirir correo
     *
     * @param idViaje
     * @param idUsuario
     */
    //crear Bitacora - Notiica
    public CoNoticia createEventNews(Integer idViaje, String idUsuario) {
        UtilLog4j.log.info(this, "createEventNews " + idViaje);
        String titulo;
        String mensaje = new String();
        String mensajeAutomatico = "";
        CoNoticia noticia = null;
        List<ComparteCon> listComparteCon = null;
        //buscar viaje
        try {
            SgViaje sgViaje = find(idViaje);
            if (sgViaje.getCoNoticia() != null) {
                UtilLog4j.log.info(this, "modificar la noticia");
                // ya tiene noticia solo modificar por estatus
                noticia = sgViaje.getCoNoticia();
                mensaje = sgViaje.getCoNoticia().getMensajeAutomatico();
//                if (!isModificacion) {
                mensaje += mostrarEstatusAprobado(sgViaje.getEstatus().getNombre(), sgViaje.getModifico().getNombre(), sgViaje.getFechaModifico(), sgViaje.getHoraModifico());
                noticia.setMensajeAutomatico(mensaje);
                coNoticiaService.editNoticia(noticia, idUsuario);
                UtilLog4j.log.info(this, "Noticia modificada..");

            } else {
                titulo = "Viaje : ".concat(sgViaje.getCodigo());
                if (sgViaje.getSgTipoEspecifico().getId() == 2) {
                    //Es terrestre
                    mensaje = mostrarDetalleViajeTerrestre(sgViaje);
                } else {
                    //Es aereo
                    mensaje = mostrarDetalleViajeAereo(sgViaje);
                }
                //prueba
                listComparteCon = new ArrayList<ComparteCon>();

                //quien creo el viaje
                //listComparteCon.add(new ComparteCon(idUsuario, "", "", "Usuario"));
                //Responsables SGL //Seguridad
                if (sgViaje.getSgTipoEspecifico().getId() != Constantes.SOLICITUDES_AEREA) {

                    List<Integer> li = new ArrayList<Integer>();
                    li.add(Constantes.SGL_RESPONSABLE);
                    li.add(Constantes.SGL_SEGURIDAD);
                    li.add(Constantes.ROL_CENTRO_OPERACION);
                    List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_DEFAULT);
                    if (lu != null) {
                        for (UsuarioRolVo usuario1 : lu) {
                            listComparteCon.add(new ComparteCon(usuario1.getIdUsuario(), "", "", "Usuario"));
                        }
                    }
                }
                //compartir con los empleados de la lista de viajeros
                List<ViajeroVO> listViajeros = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
                if (listViajeros != null) {
                    for (ViajeroVO usuario : listViajeros) {
                        if (usuario.getUsuario() != null) {
                            listComparteCon.add(new ComparteCon(usuario.getIdUsuario(), "", "", "Usuario"));
                            UtilLog4j.log.info(this, "Compartido con " + usuario.getUsuario());
                        }
                    }
                }
                //es un viaje a ciudad
                //compartir con el analista de la oficina de origen
                if (sgViaje.getSgTipoEspecifico().getId() != Constantes.SOLICITUDES_AEREA) {
                    List<SgOficinaAnalistaVo> list = sgOficinaAnalistaRemote.getAllSgOficinaAnalista(sgViaje.getSgOficina().getId(), "id", true, false);
                    for (SgOficinaAnalistaVo analista : list) {
                        listComparteCon.add(new ComparteCon(analista.getIdAnalista(), "", "", "Usuario"));
                    }
                } else {
                    listComparteCon.add(new ComparteCon(sgViaje.getGenero().getId(), "", "", "Usuario"));
                }

                if (!listComparteCon.isEmpty()) {
                    noticia = coNoticiaService.nuevaNoticia("SIA", titulo, "", mensaje, 0, 0, listComparteCon);
                    if (noticia != null) {
                        //modificar el viaje
                        try {
                            sgViaje.setCoNoticia(noticia);
                            edit(sgViaje);
                            UtilLog4j.log.info(this, "Se publico la NOTICIA con el id del viaje");
                        } catch (Exception ex) {
                            throw new SIAException("Ocurrio un error al marcar la salida del viaje");
                        }
                    }
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            noticia = null;
        }
        return noticia;
    }

    private void cancelStatusTravelNews(Integer idViaje, String motivoCancelacion, String idUsuario) {
        String titulo;
        String mensaje = "";
        CoNoticia noticia = null;
        try {
            SgViaje viaje = find(idViaje);
            noticia = viaje.getCoNoticia();
            mensaje = viaje.getCoNoticia().getMensajeAutomatico();
            //if (viaje.getEstatus().getId() == 520) {
            mensaje += "<font color=\"red\";><strong>CANCELADO</strong></font> - <font-color = \"gray\">" + Constantes.FMT_TextDate.format(viaje.getFechaModifico()) + " " + Constantes.FMT_hmm_a.format(viaje.getHoraModifico()) + "</font>";
            mensaje += "<p><strong>Motivo :</strong></p><p> " + motivoCancelacion + "<p/>";
            //}
            noticia.setMensajeAutomatico(mensaje);
            coNoticiaService.editNoticia(noticia, idUsuario);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al modificar la noticia " + e.getMessage());
        }
    }

    /**
     * ************ FIN BITACORA - CO_NOTICIA
     *
     *********************************
     * @param idOficina
     * @param status
     * @param idUsuarioGenero
     * @return
     */
    public int getCountAirTravel(int idOficina, int status, String idUsuarioGenero) {
        clearQuery();
        appendQuery("SELECT count(distinct v.id)  FROM SG_VIAJE v");
        appendQuery(" WHERE v.estatus = ").append(status);
        appendQuery(" and v.genero = '").append(idUsuarioGenero).append("'");
        appendQuery(" AND v.sg_oficina = ").append(idOficina);
        appendQuery(" AND v.sg_tipo_especifico = ").append(Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA);
        appendQuery(" AND v.id NOT IN (SELECT m.sg_viaje FROM sg_viaje_si_movimiento m)");
        appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        UtilLog4j.log.info(this, "Recupera total de viajes aereos : --> " + query.toString());
        return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
    }

    /**
     * Modifico: NLopez 07/11/2013 El adquirir correo
     *
     * @param usuario
     * @param idOficina
     * @param sgViajeIda
     * @param idRuta
     * @param lista
     * @param fromTimer
     * @param listaQuedados
     * @param fs
     * @param horaSalida
     * @param idVehiculo
     * @return MLUIS 08/11/2013
     */
    public SgViaje saveReturnTrip(Usuario usuario, int idOficina, SgViaje sgViajeIda, int idRuta, List<ViajeroVO> lista, boolean fromTimer,
            List<ViajeroVO> listaQuedados, Date fs, Date horaSalida, int idVehiculo) {
        UtilLog4j.log.info(this, "SvViajeIMpl.saveReturnTrip()");
        boolean v;
        String cc = "";
        String cco = "";
        List<ViajeroVO> vos = sgViajeroRemote.getTravellersByTravel(sgViajeIda.getId(), null);
        //  List<ViajeroVO> viajeroQuedados = new ArrayList<ViajeroVO>();
        SiMovimiento siMovimiento;
        SgViaje viajeRegreso = new SgViaje();
        viajeRegreso.setSgOficina(sgOficinaRemote.find(idOficina));
        viajeRegreso.setSgViaje(find(sgViajeIda.getId()));
        viajeRegreso.setAutobus(sgViajeIda.isAutobus());
        viajeRegreso.setVehiculoPropio(sgViajeIda.isVehiculoPropio());
        viajeRegreso.setVehiculoAsignadoEmpresa(sgViajeIda.isVehiculoAsignadoEmpresa());
        viajeRegreso.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_CREADO));
        viajeRegreso.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE));
        viajeRegreso.setResponsable(sgViajeIda.getResponsable());
        viajeRegreso.setGenero(usuario);
        viajeRegreso.setFechaGenero(new Date());
        viajeRegreso.setHoraGenero(new Date());
        viajeRegreso.setEliminado(Constantes.NO_ELIMINADO);
        viajeRegreso.setFechaProgramada(fs);
        viajeRegreso.setHoraProgramada(horaSalida);
        viajeRegreso.setRedondo(Constantes.BOOLEAN_FALSE);
        viajeRegreso.setConIntercepcion(Constantes.BOOLEAN_FALSE);

        if (sgViajeIda.getSgViajeCiudad() != null) {
            viajeRegreso.setSgViajeCiudad(sgViajeIda.getSgViajeCiudad());
            viajeRegreso.setSiAdjunto(sgViajeIda.getSiAdjunto());
            viajeRegreso.setSgRutaTerrestre(sgRutaTerrestreRemote.find(sgViajeIda.getSgRutaTerrestre().getId()));
        } else {
            viajeRegreso.setSgRutaTerrestre(sgRutaTerrestreRemote.find(idRuta));
        }
//Codigo
        viajeRegreso.setCodigo("VI" + getDigitosAño(new Date()) + "-" + String.valueOf(folioRemote.getFolio(Constantes.CONSECUTIVO_VIAJE)));

        try {

            //Crea el viaje de regreso
            create(viajeRegreso);
            createEventNews(viajeRegreso.getId(), usuario.getId());

            //Finaliza el viaje de ida
            if (sgViajeIda.getEstatus().getId() != Constantes.ESTATUS_VIAJE_PROCESO) {
                sgViajeIda.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_FINALIZAR));
            }
            sgViajeIda.setModifico(usuario);

            sgViajeIda.setFechaModifico(
                    new Date());
            sgViajeIda.setHoraModifico(
                    new Date());
            edit(sgViajeIda);

            //publicar noticia ida
            this.createEventNews(sgViajeIda.getId(), usuario.getId());

            //Crear viajeros    //Agrega viaje a los viajeros
            UtilLog4j.log.info(
                    this, "El viaje tiene : " + vos.size() + " viajeros");
            if (lista
                    != null) { // todos se regresan
                List<ViajeroVO> lTemp = new ArrayList<ViajeroVO>();
                for (ViajeroVO viajeroVO : lista) {
                    if (viajeroVO.isRedondo()) { // Si el viajero va de regreso lo
                        //incluyye en el nuevo viaje
                        lTemp.add(viajeroVO);
                    }
                }
                for (ViajeroVO viajeroVO : lTemp) {
                    //SgViajero sgViajero = sgViajeroRemote.find(viajeroVO.getId());
                    SgViajero sgViajero = new SgViajero();
                    //sgViajeroRemote.agregarViaje(usuario, sgViajero, viajeRegreso, false);
                    sgViajero.setSgSolicitudViaje(sgSolicitudViajeRemote.find(viajeroVO.getIdSolicitudViaje()));
                    sgViajero.setSgInvitado(viajeroVO.getIdInvitado() != 0 ? sgInvitadoRemote.find(viajeroVO.getIdInvitado()) : null);
                    sgViajero.setUsuario(viajeroVO.getIdInvitado() == 0 ? usuarioRemote.find(viajeroVO.getIdUsuario()) : null);
                    sgViajero.setRedondo(Constantes.BOOLEAN_FALSE);
                    sgViajero.setSgSolicitudEstancia(null);
                    sgViajero.setEstancia(Constantes.BOOLEAN_FALSE);
                    sgViajero.setEliminado(Constantes.NO_ELIMINADO);
                    sgViajero.setGrAut(Constantes.NO_ELIMINADO);
                    sgViajero.setGenero(usuario);
                    sgViajero.setFechaGenero(new Date());
                    sgViajero.setHoraGenero(new Date());
                    sgViajero.setSgViaje(viajeRegreso);
                    sgViajeroRemote.crearViajero(sgViajero, usuario.getId());
                }
            } else { //En esta opcion todos los viajeros se quedan en la oficina UtilLog4j.log.info(this, "No selecciona a nadie ninguno");
                List<ViajeroVO> lTemp = new ArrayList<ViajeroVO>();
                for (ViajeroVO viajeroVO : vos) {
                    if (viajeroVO.isRedondo()) { // Si el viajero va de regreso lo incluyye en el nuevo viaje
                        lTemp.add(viajeroVO);
                    }
                }
                sgViajeroRemote.dejaUsuarioOficinaDestinoViajeSencillo(lTemp, usuario.getId());
            }
            //Guarda en la tabla motivo todos los que no se seleccionaron del viaje
            if (listaQuedados
                    != null) { // viajeros que no se seleccionaron al crear el viaje de regreso.
                for (ViajeroVO viajeroVO : listaQuedados) {
                    if (viajeroVO.isRedondo()) {
                        listaQuedados.remove(viajeroVO);
                    }
                }
                sgViajeroRemote.dejaUsuarioOficinaDestinoViajeSencillo(listaQuedados, usuario.getId());
            }

            //Vehiculo de la empresa
            if (sgViajeIda.isVehiculoAsignadoEmpresa()) {
                try {
                    sgViajeVehiculoRemote.save(usuario, sgVehiculoRemote.find(idVehiculo), viajeRegreso);
                } catch (SIAException ex) {
                    UtilLog4j.log.info(this, "ex: " + ex.getMessage());
                    UtilLog4j.log.info(ex);
                }
            }
            if (viajeRegreso != null) {
                String para = "";
                List<UsuarioVO> lu = usuarioRemote.getUsuariosByRol(Constantes.SGL_RESPONSABLE);
                for (UsuarioVO uvo : usuarioRemote.getUsuariosByRol(Constantes.SGL_ANALISTA)) {
                    if (uvo.getId().equals(sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(Constantes.ID_OFICINA_TORRE_MARTEL).getIdAnalista())) {
                        lu.add(uvo);
                    }
                }
                int nlist = lu.size();
                int x = 1;
                para = usuario.getEmail() + ",";
                for (UsuarioVO usuario1 : lu) {
                    para += (usuario1.getMail());
                    if (x < nlist) {
                        para += ", ";
                    }
                    x++;
                }

            }
            //}
            return viajeRegreso;
            //Fin de todo
        } catch (Exception e) {
            UtilLog4j.log.info(this, "exception: " + e.getMessage());
            UtilLog4j.log.info(e);
            return null;
        }
    }

    private String getDigitosAño(Date fecha) {
        SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
        String Cadena = SDF.format(fecha);
        String r = Cadena.substring(8, 10);
        return r;
    }

    //
    public void finalizarViaje(int idViaje, String usuario) {
        try {
            SgViaje sgViaje = find(idViaje);
            sgViaje.setModifico(new Usuario(usuario));
            sgViaje.setEstatus(new Estatus(Constantes.ESTATUS_VIAJE_FINALIZAR));
            sgViaje.setFechaLlegada(new Date());
            sgViaje.setHoraLlegada(new Date());
            sgViaje.setFechaModifico(new Date());
            sgViaje.setHoraModifico(new Date());
            edit(sgViaje);

            //publicar en noticias
            createEventNews(sgViaje.getId(), usuario);

        } catch (Exception ex) {
            UtilLog4j.log.info(this, "ex: " + ex.getMessage());
        }
    }

    //
    public void cambiarEstado(int idViaje, String usuario, int status) {
        try {
            SgViaje sgViaje = find(idViaje);
            sgViaje.setModifico(new Usuario(usuario));
            sgViaje.setEstatus(estatusRemote.find(status));
            sgViaje.setFechaModifico(new Date());
            sgViaje.setHoraModifico(new Date());
            if (Constantes.ESTATUS_VIAJE_EN_DESTINO == status) {
                sgViaje.setFechaLlegada(new Date());
                sgViaje.setHoraLlegada(new Date());
            }
            edit(sgViaje);
            //publicar en noticias
            createEventNews(idViaje, usuario);

        } catch (Exception ex) {
            UtilLog4j.log.info(this, "ex: " + ex);
        }
    }

    public List<ViajeVO> getAllTripsByStatus(int status) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        List<ViajeVO> l = new ArrayList<ViajeVO>();
        List<ViajeVO> lvv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            appendQuery("SELECT distinct  v.id,"); //0
            appendQuery("v.codigo, "); //1
            appendQuery("e.nombre, "); //2
            appendQuery("v.fecha_salida, "); //3
            appendQuery("v.hora_salida, "); //4
            appendQuery("v.fecha_regreso,"); //5
            appendQuery("v.hora_regreso,"); //6
            appendQuery("u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa,"); //11
            appendQuery("o.nombre,"); //12
            appendQuery("rt.id,"); //13
            appendQuery("rt.nombre,"); //14
            //  appendQuery("'False',"); //15
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje "); //16
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //17
            appendQuery("    end, ");
            appendQuery("  v.redondo,");
            appendQuery("  v.fecha_programada, ");
            appendQuery("  v.hora_programada, ");
            appendQuery("  o.id, ");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            //
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
            appendQuery(" sg_ruta_terrestre rt");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND v.sg_tipo_especifico = 2");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_ruta_terrestre = rt.id");
            appendQuery(" AND rt.sg_oficina = o.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" Order by v.codigo asc");
            UtilLog4j.log.info(this, "Recupera los viajes or status: " + status + " --> " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                l.add(castViajeVO(objects));
            }
            clearQuery();
            appendQuery("SELECT v.id,"); //0
            appendQuery("v.codigo, "); //1
            appendQuery("e.nombre, "); //2
            appendQuery("v.fecha_salida, "); //3
            appendQuery("v.hora_salida, "); //4
            appendQuery("v.fecha_regreso,"); //5
            appendQuery("v.hora_regreso,"); //6
            appendQuery("u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa,"); //11
            appendQuery("o.nombre,"); //12
            appendQuery("0,"); //13
            appendQuery("'False',"); //14
            //     appendQuery("'False',"); //15
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje "); //16
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //17
            appendQuery("    end, ");
            appendQuery("  v.redondo,");
            appendQuery("  v.fecha_programada, ");
            appendQuery("  v.hora_programada, ");
            appendQuery(" o.id, ");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            //
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND te.id = 3");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" Order by v.codigo asc");

            List<Object[]> la = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : la) {
                lv.add(castViajeVO(objects));
            }
            clearQuery();

            appendQuery("SELECT distinct  v.id, ");//0
            appendQuery(" v.codigo,");//1
            appendQuery(" e.nombre,");//2
            appendQuery(" v.fecha_salida,");//3
            appendQuery(" v.hora_salida,");//4
            appendQuery(" v.fecha_regreso, ");//5
            appendQuery(" v.hora_regreso,");//6
            appendQuery(" u.nombre as responsable, ");//7
            appendQuery(" te.nombre as tipo,  ");//8
            appendQuery(" v.autobus,  ");//9
            appendQuery(" v.vehiculo_propio,  ");//10
            appendQuery(" v.vehiculo_asignado_empresa, ");//11
            appendQuery(" o.nombre, ");//12
            appendQuery(" vc.id,");//13
            appendQuery(" ciu.nombre||','||es.nombre||','||pa.nombre,");//14
            //   appendQuery(" 'False', ");//15
            appendQuery(" case when v.sg_viaje is null then 0");
            appendQuery("   when v.sg_viaje is not null then v.sg_viaje  ");
            appendQuery("  end, ");
            appendQuery(" case when v.si_adjunto is null then 0");
            appendQuery("   when v.si_adjunto is not null then v.si_adjunto  ");
            appendQuery("  end,");
            appendQuery("  v.redondo,");
            appendQuery("  v.fecha_programada, ");
            appendQuery("  v.hora_programada, ");
            appendQuery("  o.id, ");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            //
            appendQuery(" FROM SG_VIAJE v, ");
            appendQuery(" Estatus e, ");
            appendQuery(" usuario u, ");
            appendQuery(" sg_oficina o, ");
            appendQuery(" sg_tipo_especifico te,");
            appendQuery(" SG_VIAJE_CIUDAD vc,");
            appendQuery(" SI_CIUDAD ciu,");
            appendQuery(" SI_PAIS pa,");
            appendQuery(" SI_ESTADO es");
            appendQuery(" WHERE v.estatus =").append(status);
            appendQuery(" AND v.sg_tipo_especifico = 2");
            appendQuery(" AND v.SG_VIAJE_CIUDAD = vc.ID");
            appendQuery(" AND vc.SI_CIUDAD = ciu.ID");
            appendQuery(" AND ciu.SI_ESTADO = es.id");
            appendQuery(" AND ciu.SI_PAIS = pa.ID");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" Order by v.codigo asc");
            List<Object[]> lc = em.createNativeQuery(query.toString()).getResultList();
            UtilLog4j.log.info(this, "Total viajes a ciudades " + lc.size());
            for (Object[] objects : lc) {
                lv.add(castViajeVO(objects));
            }

            lv.addAll(l);
            lvv.addAll(lv);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
        }
        return lvv;
    }

    /**
     * @param status
     * @return
     * @MLUIS @ 02/12/2013
     */
    public List<ViajeVO> getAllTripsByStatusOffice(int status) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            appendQuery("SELECT distinct  v.id,"); //0
            appendQuery("v.codigo, "); //1
            appendQuery("e.nombre, "); //2
            appendQuery("v.fecha_salida, "); //3
            appendQuery("v.hora_salida, "); //4
            appendQuery("v.fecha_regreso,"); //5
            appendQuery("v.hora_regreso,"); //6
            appendQuery("u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa,"); //11
            appendQuery("o.nombre,"); //12
            appendQuery("rt.id,"); //13
            appendQuery("rt.nombre,"); //14
            //     appendQuery("'False',"); //
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje "); //15
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //16
            appendQuery("    end, ");
            appendQuery("  v.redondo,");//17
            appendQuery("  v.fecha_programada, ");//18
            appendQuery("  v.hora_programada , ");//19
            appendQuery("  o.id, ");//20
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");//21

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22

            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
            appendQuery(" sg_ruta_terrestre rt");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_viaje_ciudad is null");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_ruta_terrestre = rt.id");
            appendQuery(" AND rt.sg_oficina = o.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" Order by v.codigo asc");
            UtilLog4j.log.info(this, "Recupera los viajes or status: " + status + " --> " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                lv.add(castViajeVO(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
        }
        return lv;
    }

    public List<ViajeVO> getAllTripsByStatusAir(int status) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            appendQuery("SELECT v.id,"); //0
            appendQuery("v.codigo, "); //1
            appendQuery("e.nombre, "); //2
            appendQuery("v.fecha_salida, "); //3
            appendQuery("v.hora_salida, "); //4
            appendQuery("v.fecha_regreso,"); //5
            appendQuery("v.hora_regreso,"); //6
            appendQuery("u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa,"); //11
            appendQuery("o.nombre,"); //12
            appendQuery("0,"); //13
            appendQuery("'False',"); //14
            //    appendQuery("'False',"); //
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje "); //15
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //16
            appendQuery("    end, ");
            appendQuery("  v.redondo,");//17
            appendQuery("  v.fecha_programada, ");//18
            appendQuery("  v.hora_programada , ");//19
            appendQuery("  o.id, ");//20
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");//21

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22

            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND te.id = ").append(Constantes.SOLICITUDES_AEREA);
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" Order by v.codigo asc");

            List<Object[]> la = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : la) {
                lv.add(castViajeVO(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
        }
        return lv;
    }

    public List<ViajeVO> getAllTripsByStatusCities(int status) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            appendQuery("SELECT distinct  v.id, ");//0
            appendQuery(" v.codigo,"); //1
            appendQuery(" e.nombre,"); //2
            appendQuery(" v.fecha_salida,"); //3
            appendQuery(" v.hora_salida,"); //4
            appendQuery(" v.fecha_regreso, "); //5
            appendQuery(" v.hora_regreso,"); //6
            appendQuery(" u.nombre as responsable,  "); //7
            appendQuery(" te.nombre as tipo,  "); //8
            appendQuery(" v.autobus,  "); //9
            appendQuery(" v.vehiculo_propio,  "); //10
            appendQuery(" v.vehiculo_asignado_empresa, "); //11
            appendQuery(" o.nombre, "); //12
            appendQuery(" vc.id,"); //13
            appendQuery(" ciu.nombre||','||es.nombre||','||pa.nombre,"); //14
            appendQuery(" case when v.sg_viaje is null then 0");
            appendQuery("   when v.sg_viaje is not null then v.sg_viaje  ");
            appendQuery("  end, ");//15
            appendQuery(" case when v.si_adjunto is null then 0");
            appendQuery("   when v.si_adjunto is not null then v.si_adjunto  ");
            appendQuery("  end,");//16
            appendQuery("  v.redondo,");//17
            appendQuery("  v.fecha_programada, ");//18
            appendQuery("  v.hora_programada , ");//19
            appendQuery("  o.id, ");//20
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");//21

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22

            appendQuery(" FROM SG_VIAJE v, ");
            appendQuery(" Estatus e, ");
            appendQuery(" usuario u, ");
            appendQuery(" sg_oficina o, ");
            appendQuery(" sg_tipo_especifico te,");
            appendQuery(" SG_VIAJE_CIUDAD vc,");
            appendQuery(" SI_CIUDAD ciu,");
            appendQuery(" SI_PAIS pa,");
            appendQuery(" SI_ESTADO es");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery(" AND v.SG_VIAJE_CIUDAD = vc.ID");
            appendQuery(" AND vc.SI_CIUDAD = ciu.ID");
            appendQuery(" AND ciu.SI_ESTADO = es.id");
            appendQuery(" AND ciu.SI_PAIS = pa.ID");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" Order by v.codigo asc");
            List<Object[]> lc = em.createNativeQuery(query.toString()).getResultList();
            UtilLog4j.log.info(this, "Total viajes a ciudades " + lc.size());
            for (Object[] objects : lc) {
                lv.add(castViajeVO(objects));
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
        }
        return lv;
    }

    public int getCountTripsByStatus(int status, boolean isTerrestre) {
        try {
            if (isTerrestre) {
                clearQuery();
                appendQuery("SELECT count(distinct v.id)");
                appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
                appendQuery(" sg_ruta_terrestre rt");
                appendQuery(" WHERE v.estatus = ").append(status);
                appendQuery(" AND v.sg_tipo_especifico = 2");
                appendQuery(" AND v.estatus = e.id");
                appendQuery(" AND v.responsable = u.id");
                appendQuery(" AND v.sg_tipo_especifico = te.id");
                appendQuery(" AND v.sg_oficina = o.id");
                appendQuery(" AND v.sg_ruta_terrestre = rt.id");
                appendQuery(" AND rt.sg_oficina = o.id");
                appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
                return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
            } else {
                clearQuery();
                appendQuery("SELECT count(distinct v.id)");
                appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te");
                appendQuery(" WHERE v.estatus = ").append(status);
                appendQuery(" AND te.id = 3");
                appendQuery(" AND v.estatus = e.id");
                appendQuery(" AND v.responsable = u.id");
                appendQuery(" AND v.sg_oficina = o.id");
                appendQuery(" AND v.sg_tipo_especifico = te.id");
                appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
                return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
            return 0;
        }
    }

    public int getCountTripsByStatusOffice(int status, boolean isTerrestre) {
        try {
            clearQuery();
            appendQuery("SELECT count(distinct v.id)");
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
            appendQuery(" sg_ruta_terrestre rt");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND v.sg_tipo_especifico = 2");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_viaje_ciudad is null");
            appendQuery(" AND v.sg_ruta_terrestre = rt.id");
            appendQuery(" AND rt.sg_oficina = o.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

            return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
            return 0;
        }
    }

    public int getCountTripsByStatusAir(int status, boolean isTerrestre) {
        try {
            clearQuery();
            appendQuery("SELECT count(distinct v.id)");
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND te.id = ").append(Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA);
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
            return 0;
        }
    }

    public int getCountAllTripsViajeCiudadByStatus(int idEstatus) {
        UtilLog4j.log.info(this, "SgViajeImpl.getCountAllTripsViajeCiudadByStatus()");
        clearQuery();
        appendQuery("SELECT count(distinct  v.id) ");
        appendQuery(" FROM SG_VIAJE v, ");
        appendQuery(" Estatus e, ");
        appendQuery(" usuario u, ");
        appendQuery(" sg_oficina o, ");
        appendQuery(" sg_tipo_especifico te,");
        appendQuery(" SG_VIAJE_CIUDAD vc,");
        appendQuery(" SI_CIUDAD ciu,");
        appendQuery(" SI_PAIS pa,");
        appendQuery(" SI_ESTADO es");
        appendQuery(" WHERE v.estatus =").append(idEstatus);
        appendQuery(" AND v.sg_tipo_especifico = 2");
        appendQuery(" AND v.SG_VIAJE_CIUDAD = vc.ID");
        appendQuery(" AND vc.SI_CIUDAD = ciu.ID");
        appendQuery(" AND ciu.SI_ESTADO = es.id");
        appendQuery(" AND ciu.SI_PAIS = pa.ID");
        appendQuery(" AND v.estatus = e.id");
        appendQuery(" AND v.responsable = u.id");
        appendQuery(" AND v.sg_tipo_especifico = te.id");
        appendQuery(" AND v.sg_oficina = o.id");
        appendQuery(" AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");

        return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
    }

    public List<ViajeVO> getTripsByStatusAndDate(int status, Date fechaInicio, Date fechaFin) {
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        List<ViajeVO> l = new ArrayList<ViajeVO>();
        List<ViajeVO> lvv = new ArrayList<ViajeVO>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String inicio = sdf.format(fechaInicio);
        String fin = sdf.format(fechaFin);
        try {
            clearQuery();
            appendQuery("SELECT distinct  v.id,"); //0
            appendQuery("v.codigo, "); //1
            appendQuery("e.nombre, "); //2
            appendQuery("v.fecha_salida, "); //3
            appendQuery("v.hora_salida, "); //4
            appendQuery("v.fecha_regreso,"); //5
            appendQuery("v.hora_regreso,"); //6
            appendQuery("u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa,"); //11
            appendQuery("o.nombre,"); //12
            appendQuery("rt.id,"); //13
            appendQuery("rt.nombre,"); //14
            //     appendQuery("'False',"); //15
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje "); //16);
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //17
            appendQuery("    end ,");
            appendQuery("  v.redondo,");
            appendQuery("  v.fecha_programada, ");
            appendQuery("  v.hora_programada , ");
            appendQuery("  o.id, ");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
            appendQuery(" sg_ruta_terrestre rt");
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND v.sg_tipo_especifico = 2");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_ruta_terrestre = rt.id");
            appendQuery(" AND rt.sg_oficina = o.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND v.fecha_modifico  between cast('").append(inicio).append("' as date) and cast('").append(fin).append("' as date)").append(" Order by v.codigo asc");
            UtilLog4j.log.info(this, "Recupera los viajes or status y rango de fechas: " + status + " --> " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                l.add(castViajeVO(objects));
            }
            clearQuery();

            appendQuery("SELECT v.id,"); //0
            appendQuery("v.codigo, "); //1
            appendQuery("e.nombre, "); //2
            appendQuery("v.fecha_salida, "); //3
            appendQuery("v.hora_salida, "); //4
            appendQuery("v.fecha_regreso,"); //5
            appendQuery("v.hora_regreso,"); //6
            appendQuery("u.nombre as responsable, "); //7
            appendQuery("te.nombre as tipo, "); //8
            appendQuery("v.autobus, "); //9
            appendQuery("v.vehiculo_propio, "); //10
            appendQuery("v.vehiculo_asignado_empresa,"); //11
            appendQuery("o.nombre,"); //12
            appendQuery("0,"); //13
            appendQuery("'False',"); //14
            //    appendQuery("'False',"); //15
            appendQuery("case when v.sg_viaje is null then 0");
            appendQuery("     when v.sg_viaje is not null then v.sg_viaje "); //16
            appendQuery("    end, ");
            appendQuery("case when v.si_adjunto is null then 0");
            appendQuery("     when v.si_adjunto is not null then v.si_adjunto "); //17
            appendQuery("    end ,");
            appendQuery("  v.redondo,");
            appendQuery("  v.fecha_programada, ");
            appendQuery("  v.hora_programada , ");
            appendQuery("  o.id, ");
            appendQuery("  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,");

            appendQuery(" CASE WHEN v.SI_ADJUNTO is null THEN ''");
            appendQuery(" WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   ");
            appendQuery(" END AS uuid_adjunto");//22
            //
            appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te");
            appendQuery(" WHERE v.estatus = " + status);
            appendQuery(" AND te.id = 3");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'").append(" AND v.fecha_modifico  between cast('").append(inicio).append("' as date) and cast('").append(fin).append("' as date)");
            appendQuery(" Order by v.codigo asc");
            UtilLog4j.log.info(this, "Recupera los viajes aereos or status y rango de fechas: " + status + " --> " + query.toString());
            List<Object[]> la = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : la) {
                lv.add(castViajeVO(objects));
            }
            lv.addAll(l);
            lvv.addAll(lv);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
        }
        return lvv;
    }

    public int getCountTripsByStatusAndDate(int status, Date fechaInicio, Date fechaFin, boolean isTerrestre) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        String inicio = sdf.format(fechaInicio);
        String fin = sdf.format(fechaFin);

        try {

            if (isTerrestre) {
                clearQuery();
                appendQuery("SELECT count(distinct v.id)"); //0
                appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te,");
                appendQuery(" sg_ruta_terrestre rt");
                appendQuery(" WHERE v.estatus = ").append(status);
                appendQuery(" AND v.sg_tipo_especifico = 2");
                appendQuery(" AND v.estatus = e.id");
                appendQuery(" AND v.responsable = u.id");
                appendQuery(" AND v.sg_tipo_especifico = te.id");
                appendQuery(" AND v.sg_oficina = o.id");
                appendQuery(" AND v.sg_ruta_terrestre = rt.id");
                appendQuery(" AND rt.sg_oficina = o.id");
                appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
                appendQuery(" AND v.fecha_modifico  between cast('").append(inicio).append("' as date) and cast('").append(fin).append("' as date)").append(" ");

                return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
            } else {
                clearQuery();
                appendQuery("SELECT count(distinct v.id)"); //0
                appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te");
                appendQuery(" WHERE v.estatus = ").append(status);
                appendQuery(" AND te.id = 3");
                appendQuery(" AND v.estatus = e.id");
                appendQuery(" AND v.responsable = u.id");
                appendQuery(" AND v.sg_oficina = o.id");
                appendQuery(" AND v.sg_tipo_especifico = te.id");
                appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'").append(" AND v.fecha_modifico  between cast('").append(inicio).append("' as date) and cast('").append(fin).append("' as date)");

                return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error: " + e.getMessage());
            return 0;
        }
    }

    public SiMovimiento findMotivoCancelacion(Integer idViaje) {
        try {
            return (SiMovimiento) em.createQuery("SELECT m.siMovimiento FROM SgViajeSiMovimiento m WHERE m.sgViaje.id = :idViaje"
                    + "  AND m.eliminado = :eli ").setParameter("idViaje", idViaje).setParameter("eli", Constantes.BOOLEAN_FALSE).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al buscar el motivo de cancelacion en viaje " + e.getMessage());
            return null;
        }
    }

    public void detenerViaje(Usuario usuario, SgViaje sgViaje, String motivo) {
        try {
            boolean v = false;
            List<ViajeroVO> l = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
            if (usuario.getId().equals("PRUEBA")) {
                v = notificacionViajeRemote.sendMailStopTrip(usuario.getEmail(), "", sgViaje, l, motivo);
            } else {
                v = notificacionViajeRemote.sendMailStopTrip(sgViaje.getResponsable().getEmail(), correoCopia(sgViaje.getGenero().getEmail(), sgViaje), sgViaje, l, motivo);
            }
            if (v) {
                sgViaje.setEstatus(estatusRemote.find(501));
                sgViaje.setModifico(usuario);
                sgViaje.setFechaModifico(new Date());
                sgViaje.setHoraModifico(new Date());
                edit(sgViaje);
                //Publica noticia
                createEventNews(sgViaje.getId(), usuario.getId());
                //Guarda en movimiento
                SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(7), usuario);
                //Guarda en  viaje movimiento
                sgViajeSiMovimientoRemote.guardarViajeCancelado(usuario, sgViaje, siMovimiento);
            }
        } catch (Exception ex) {
            UtilLog4j.log.info(this, "Exc: detener viaje " + ex.getMessage());
        }

    }

    //Tipo  5 : Copiados
    // Tipo 7:  usuario copiados de direccion general
    private String publicaViaje(int idTipo, int idOficina) {
        UtilLog4j.log.info(this, "publicaViaje");
        String correo = "";
        String nombre = "";

        List<UsuarioTipoVo> luc = siUsuarioCopiadoRemote.getListUser(idTipo, idOficina);

        for (UsuarioTipoVo usuarioCopiadoVo : luc) {
//            UtilLog4j.log.info(this, "Id usuario copiado "+(usuarioCopiadoVo.getUsuario() == null ? "Es null el usuario":usuarioCopiadoVo.getNombre()));
            UtilLog4j.log.info(this, "correo usuario copiado " + (usuarioCopiadoVo.getCorreo() == null ? "Es null " : usuarioCopiadoVo.getCorreo()));
            if (correo.isEmpty()) {
//                nombre = usuarioCopiadoVo.getUsuario();
                correo = usuarioCopiadoVo.getCorreo();
            } else {
                correo += "," + usuarioCopiadoVo.getCorreo();
//                nombre += "; " + usuarioCopiadoVo.getUsuario();
            }
        }
        UtilLog4j.log.info(this, "Correo viaje publicado. " + correo);
//        UtilLog4j.log.info(this, "Correo viaje publicado. " + nombre);
        return correo;
    }

    private String correoCopiaOculta(List<ViajeroVO> lista) {
        String cco = "";
        for (ViajeroVO viajeroVO : lista) {
            if (cco.isEmpty()) {
                cco = (viajeroVO.getCorreo() != null ? viajeroVO.getCorreo() : "");
            } else {
                cco += (viajeroVO.getCorreo() != null ? ("," + viajeroVO.getCorreo()) : "");
            }
        }
        return cco;
    }

    public List<ViajeVO> getAllSgViajeForAutomaticReturn(int estatus, Date fechaDiaDespues) {
        UtilLog4j.log.info(this, "SgViajeImpl.getAllSgViajeForAutomaticReturn()");

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        List<ViajeVO> lv = null;
        List<Object[]> l;
        ViajeVO o;

        try {
            clearQuery();
            appendQuery("SELECT v.id, v.codigo");
            appendQuery(" FROM  SG_VIAJE v ");
            appendQuery(" WHERE v.estatus = ").append(estatus);
            appendQuery(" AND  v.fecha_salida = '").append(sdf.format(fechaDiaDespues)).append("';");

            UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());

            l = em.createNativeQuery(query.toString()).getResultList();

            UtilLog4j.log.info(this, "Size " + l.size());

            if (l != null && !l.isEmpty()) {
                lv = new ArrayList<ViajeVO>();
                for (Object[] objects : l) {
                    o = new ViajeVO();
                    o.setId((Integer) objects[0]);
                    lv.add(o);
                }
            }
            UtilLog4j.log.info(this, "Saliendo ");
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al traer los viajes por salir" + e.getMessage());
            return null;
        }
        return lv;
    }

    /**
     * Joel Rodriguez 27.02.2014
     *
     * La seleccion ahora se realiza por (fecha_programada = hoy AND hora >
     * Realiza_Operacion) OR fecha_programada = Mañana esto para que solo afecte
     * los viajes con hora de salida proxima al cambio de semaforo.
     *
     * @param status
     * @param idRuta
     * @param dias
     * @return
     */
    public List<ViajeVO> traerViajesPorRuta(int status, int idRuta, int dias, boolean traerNoticias, String usrGerente, int viajeID) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = null;
        clearQuery();
        appendQuery("SELECT v.id, v.codigo, u.NOMBRE, "
                + " case "
                + " when v.AUTOBUS = 'True' then 'Autobus' "
                + " when v.VEHICULO_PROPIO = 'True' then 'Propio'  "
                + " when v.VEHICULO_ASIGNADO_EMPRESA = 'True' then 'Asignado'  "
                + " else 'Sin vehículo' "
                + " end  "
                + " ,case when vv.NUMERO_PLACA  is NOT null then vv.NUMERO_PLACA when vv.NUMERO_PLACA  is null then (SELECT ve.NUMERO_PLACA from SG_VIAJE_VEHICULO a inner join SG_VEHICULO ve on ve.id = a.SG_VEHICULO where a.SG_VIAJE = v.ID) else 'N/A' END "
                + " ,u.TELEFONO "
                + " ,v.co_noticia, v.FECHA_SALIDA, v.HORA_SALIDA, v.FECHA_PROGRAMADA, v.HORA_PROGRAMADA, r.TIEMPO_VIAJE, v.estatus "
                + " FROM  SG_VIAJE v "
                + " inner join USUARIO u on u.id = v.RESPONSABLE and u.ELIMINADO = 'False' "
                + " left join SG_VIAJE_VEHICULO av on av.SG_VIAJE = v.id and av.ELIMINADO = 'False' "
                + " left join SG_VEHICULO vv on vv.id = av.SG_VEHICULO and vv.ELIMINADO = 'False' "
                + " left join SG_RUTA_TERRESTRE r on r.id = v.sg_ruta_terrestre and r.ELIMINADO = 'False' "
                + " WHERE v.ELIMINADO = false ");
        if (status > 0) {
            appendQuery(" AND  v.estatus = ").append(status);
        }
        if (idRuta > 0) {
            appendQuery(" AND  v.sg_ruta_terrestre = ").append(idRuta);
        }
        if (viajeID > 0) {
            appendQuery(" AND  v.id = ").append(viajeID);
        }
        if (viajeID == 0) {
            appendQuery(" and v.ID not in (select SG_VIAJE_A from GR_INTERSECCION where ELIMINADO = 'False')"
                    + " and v.ID not in (select SG_VIAJE_B from GR_INTERSECCION where ELIMINADO = 'False')"
                    + " AND  v.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
        }

        if (dias > 0) {
            appendQuery(" and v.FECHA_PROGRAMADA >= cast('now' as date) "
                    + " and (v.FECHA_PROGRAMADA <= (SELECT CURRENT_DATE + INTERVAL '" + dias + " day'))");
        } else {
            appendQuery(" and ((v.FECHA_PROGRAMADA is not null and v.FECHA_PROGRAMADA <= cast('Now' as date))  or (v.FECHA_SALIDA is not null and v.FECHA_SALIDA <= cast('Now' as date))) ");
        }

        if (usrGerente != null && !usrGerente.isEmpty()) {
            appendQuery("and 0 < ( "
                    + "select count(vi.id) "
                    + "from SG_VIAJERO vi "
                    + "left join USUARIO ur on ur.id = vi.USUARIO and ur.ELIMINADO = 'False' and ur.GERENCIA in (select GERENCIA  "
                    + "from AP_CAMPO_GERENCIA  "
                    + "where RESPONSABLE = '").append(usrGerente).append("' "
                    + "and ELIMINADO = 'False') "
                    + "where vi.ELIMINADO = 'False' "
                    + "and vi.SG_VIAJE = v.ID "
                    + ") ");

        }

        /*
         */
        UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());

        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null) {
            lv = new ArrayList<ViajeVO>();
            for (Object[] objects : l) {
                ViajeVO o = new ViajeVO();
                o.setId((Integer) objects[0]);
                o.setCodigo((String) objects[1]);
                o.setResponsable((String) objects[2]);
                o.setVehiculo((String) objects[3]);
                o.setVehiculoPlaca((String) objects[4]);
                o.setResponsableTel((String) objects[5]);
                o.setIdNoticia((Integer) objects[6]);
                o.setFechaSalida(objects[7] != null ? (Date) objects[7] : null);
                o.setHoraSalida(objects[8] != null ? (Date) objects[8] : null);
                o.setFechaProgramada(objects[9] != null ? (Date) objects[9] : null);
                o.setHoraProgramada(objects[10] != null ? (Date) objects[10] : null);
                if (traerNoticias && o.getIdNoticia() != null && o.getIdNoticia() > 0) {
                    NoticiaVO noticia = new NoticiaVO();
                    noticia.setListaComentario(coComentarioRemote.traerComentariosPorNoticia(o.getIdNoticia()));
                    o.setNoticia(noticia);
                }
                o.setTiempoViaje((String) objects[11]);
                if (o.getFechaSalida() != null && o.getHoraSalida() != null) {
                    o.setTiempoViajeRealVal();
                }
                o.setIdEstatus((Integer) objects[12]);
                lv.add(o);
            }
        }
        return lv;
    }

    public List<ViajeVO> traerViajesInterceptantes(int status, int idViajeA, Date fecha1, Date fecha2) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = null;
        clearQuery();
        appendQuery(" SELECT DISTINCT v.id, v.codigo, u.NOMBRE, "
                + " case "
                + " when v.AUTOBUS = 'True' then 'Autobus' "
                + " when v.VEHICULO_PROPIO = 'True' then 'Propio'  "
                + " when v.VEHICULO_ASIGNADO_EMPRESA = 'True' then 'Asignado'  "
                + " else 'Sin vehículo' "
                + " end  "
                + " ,case when vv.NUMERO_PLACA  is NOT null then vv.NUMERO_PLACA when vv.NUMERO_PLACA  is null then (SELECT ve.NUMERO_PLACA from SG_VIAJE_VEHICULO a inner join SG_VEHICULO ve on ve.id = a.SG_VEHICULO where a.SG_VIAJE = v.ID) else 'N/A' END "
                + " ,u.TELEFONO "
                + " ,v.co_noticia, v.FECHA_SALIDA, v.HORA_SALIDA, v.FECHA_PROGRAMADA, v.HORA_PROGRAMADA, v.CON_INTERCEPCION"
                + " FROM  SG_VIAJE v "
                + " inner join USUARIO u on u.id = v.RESPONSABLE and u.ELIMINADO = 'False' "
                + " inner join SG_RUTA_TERRESTRE r on r.id = v.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False' "
                + " inner join GR_RUTAS_ZONAS rz on rz.SG_RUTA_TERRESTRE = r.id and rz.ELIMINADO = 'False' "
                + " inner join GR_RUTAS_ZONAS rzz on rzz.GR_MAPA = rz.GR_MAPA and rzz.ELIMINADO = 'False' "
                + " left join SG_VIAJE_VEHICULO av on av.SG_VIAJE = v.id and av.ELIMINADO = 'False' "
                + " left join SG_VEHICULO vv on vv.id = av.SG_VEHICULO and vv.ELIMINADO = 'False' "
                + " WHERE v.estatus = ").append(status).append(
                " and v.ID <> ").append(idViajeA).append(
                " and v.ID not in (select SG_VIAJE_A from GR_INTERSECCION where ELIMINADO = 'False')"
                + " and v.ID not in (select SG_VIAJE_B from GR_INTERSECCION where ELIMINADO = 'False')"
                + " AND  v.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");

        if (fecha1 != null && fecha2 != null) {
            appendQuery(" and v.fecha_programada >=  '").append(Constantes.FMT_yyyy_MM_dd.format(fecha1)).append(
                    "'  and v.fecha_programada <= '").append(Constantes.FMT_yyyy_MM_dd.format(fecha2)).append("' ");
        } else {
            appendQuery(" and ((v.FECHA_PROGRAMADA is not null and v.FECHA_PROGRAMADA <= cast('Now' as date))  or (v.FECHA_SALIDA is not null and v.FECHA_SALIDA <= cast('Now' as date))) ");
        }
        /*
         */
        UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());

        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null) {
            lv = new ArrayList<ViajeVO>();
            for (Object[] objects : l) {
                ViajeVO o = new ViajeVO();
                o.setId((Integer) objects[0]);
                o.setCodigo((String) objects[1]);
                o.setResponsable((String) objects[2]);
                o.setVehiculo((String) objects[3]);
                o.setVehiculoPlaca((String) objects[4]);
                o.setResponsableTel((String) objects[5]);
                o.setIdNoticia((Integer) objects[6]);
                o.setFechaSalida(objects[7] != null ? (Date) objects[7] : null);
                o.setHoraSalida(objects[8] != null ? (Date) objects[8] : null);
                o.setFechaProgramada(objects[9] != null ? (Date) objects[9] : null);
                o.setHoraProgramada(objects[10] != null ? (Date) objects[10] : null);
                o.setConInter((Boolean) objects[11]);
                lv.add(o);
            }
        }
        return lv;
    }

    public List<SgViaje> traerViajesPorRutaCancelar(int status, int idRuta, int idZona, int idSemaforo, boolean useSemaforo) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<SgViaje> lv = null;
        try {
            clearQuery();

            appendQuery(" SELECT v.id ");
            appendQuery(" FROM  SG_VIAJE v  ");
            appendQuery(" inner join GR_RUTAS_ZONAS r on r.SG_RUTA_TERRESTRE = v.SG_RUTA_TERRESTRE ");
            if (useSemaforo && Constantes.ID_COLOR_SEMAFORO_ROJO == idSemaforo) {
                appendQuery(" and r.CANCELA = 'True' ");
            } else if (useSemaforo && Constantes.ID_COLOR_SEMAFORO_NEGRO == idSemaforo) {
                appendQuery(" and r.CANCELASN = 'True' ");
            }
            appendQuery(" WHERE v.estatus = ").append(status);
            appendQuery(" AND  v.sg_ruta_terrestre = ").append(idRuta);
            appendQuery(" AND  v.eliminado  = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" and ((v.FECHA_PROGRAMADA is not null and v.FECHA_PROGRAMADA <= cast('Now' as date))  or (v.FECHA_SALIDA is not null and v.FECHA_SALIDA <= cast('Now' as date))) ");
            appendQuery(" and r.GR_MAPA = ").append(idZona);
            appendQuery(" group by v.id; ");

            UtilLog4j.log.info(this, "Recupera los viajes por salir para ser cancelados" + query.toString());

            List<Object> l = em.createNativeQuery(query.toString()).getResultList();

            if (l != null) {
                lv = new ArrayList<SgViaje>();
                for (Object object : l) {
                    SgViaje o = this.find((Integer) object);
                    lv.add(o);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lv = new ArrayList<SgViaje>();
        }
        return lv;
    }

    public List<ViajeVO> traerViajesTerrestrePorEstatus(int status, boolean conViajeros) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = null;
        clearQuery();
        appendQuery("select v.id, v.CODIGO, v.FECHA_PROGRAMADA, v.HORA_PROGRAMADA, v.fecha_regreso, v.hora_regreso,");
        appendQuery(" o.NOMBRE, v.redondo,");
        appendQuery(" case when v.sg_ruta_terrestre is null then '' else (select rt.NOMBRE from SG_RUTA_TERRESTRE rt where v.sg_ruta_terrestre = rt.id)  end as Ruta,");
        appendQuery(" u.NOMBRE,v.AUTOBUS, v.VEHICULO_PROPIO,  ");
        appendQuery(" v.VEHICULO_ASIGNADO_EMPRESA, case when v.SI_ADJUNTO is null then 0 else v.SI_ADJUNTO  end as Adjunto, ");
        appendQuery(" case when v.SG_VIAJE_CIUDAD is null then '' else (select c.NOMBRE from SG_VIAJE_CIUDAD vc, SI_CIUDAD c ");
        appendQuery(" where v.SG_VIAJE_CIUDAD = vc.ID and vc.SI_CIUDAD = c.ID and c.ELIMINADO = 'False') end as Ciudad");
        appendQuery(" from SG_VIAJE v, SG_OFICINA o, USUARIO u  ");
        appendQuery(" where v.ESTATUS = ").append(status).append(" and v.FECHA_PROGRAMADA = (SELECT CURRENT_DATE + INTERVAL '1 day')");
        appendQuery(" and v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
        appendQuery(" and v.SG_OFICINA = o.ID  and v.RESPONSABLE = u.ID ");
        appendQuery(" and v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'").append(" order by o.nombre asc ");
        //
        UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());
        //
        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null) {
            lv = new ArrayList<ViajeVO>();
            for (Object[] objects : l) {
                ViajeVO o = new ViajeVO();
                o.setId((Integer) objects[0]);
                o.setCodigo((String) objects[1]);
                o.setFechaProgramada((Date) objects[2]);
                o.setHoraProgramada((Date) objects[3]);
                o.setFechaRegreso((Date) objects[4]);
                o.setHoraRegreso((Date) objects[5]);
                o.setOficina((String) objects[6]);
                o.setRedondo((Boolean) objects[7]);
                o.setRuta((String) objects[8]);
                o.setResponsable((String) objects[9]);
                o.setAutobus((Boolean) objects[10]);
                o.setVehiculoPropio((Boolean) objects[11]);
                o.setVehiculoEmpresa((Boolean) objects[12]);
                o.setSiAdjunto((Integer) objects[13]);
                o.setDestinoCiudad((String) objects[14]);
                if (conViajeros) {
                    o.setListaViajeros(sgViajeroRemote.getTravellersByTravel(o.getId(), null));
                }
                lv.add(o);
            }
        }
        return lv;
    }

    public ViajeVO buscarPorId(int idViaje, boolean conViajeros) {
        try {
            return this.buscarPorId(idViaje, conViajeros, false, false);
        } catch (Exception nure) {
            UtilLog4j.log.fatal(this, nure.getMessage());
            return null;
        }
    }

    public ViajeVO buscarPorId(int idViaje, boolean conViajeros, boolean conNoticia, boolean detRuta) {
        clearQuery();
        try {
            query.append(consultaViaje());
            query.append("  WHERE v.id = ").append(idViaje);
            query.append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] result = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castViajeBuscar(result, conViajeros, conNoticia, detRuta);

        } catch (Exception nure) {
            UtilLog4j.log.fatal(this, nure.getMessage());
            return null;
        }
    }

    private ViajeVO castViajeBuscar(Object[] result, boolean conViajeros, boolean conNoticia, boolean detRuta) {
        ViajeVO vo = new ViajeVO();
        vo.setId((Integer) result[0]);
        vo.setCodigo((String) result[1]);
        vo.setStatus((String) result[2]);
        vo.setFechaSalida((Date) result[3]);
        vo.setHoraSalida((Date) result[4]);
        vo.setFechaRegreso((Date) result[5]);
        vo.setHoraRegreso((Date) result[6]);
        vo.setResponsable((String) result[7]);
        vo.setTipo((String) result[8]);
        vo.setAutobus((Boolean) result[9]);
        vo.setVehiculoPropio((Boolean) result[10]);
        vo.setVehiculoEmpresa((Boolean) result[11]);
        vo.setOficina((String) result[12]);
        vo.setViajeIda(result[13] == null ? 0 : (Integer) result[13]);
        vo.setIdNoticia(result[14] != null ? (Integer) result[14] : 0);
        vo.setIdEstatus((Integer) result[15]);
        vo.setIdTipoEspecifico((Integer) result[16]);
        vo.setFechaProgramada((Date) result[17]);
        vo.setHoraProgramada((Date) result[18]);
        vo.setIdRuta(result[19] != null ? (Integer) result[19] : 0);
        vo.setRuta((String) result[20]);
        vo.setRedondo((Boolean) result[21]);
        vo.setIdItinerario(result[22] != null ? (Integer) result[22] : 0);
        vo.setIdAdjunto(result[23] != null ? (Integer) result[23] : 0);
        vo.setAdjunto((String) result[24]);
        vo.setUuid((String) result[25]);
        vo.setTipoRuta(result[26] != null ? (Integer) result[26] : 0);
        vo.setIdSgViajeCiudad(result[27] != null ? (Integer) result[27] : 0);
        vo.setResponsableTel((String) result[28]);
        vo.setIdOficinaOrigen((Integer) result[29]);
        vo.setConChofer((Boolean) result[43]);
        vo.setSgViaje(result[44] != null ? (Integer) result[44] : 0);
        //Destino
        if (vo.getTipoRuta() == Constantes.RUTA_TIPO_OFICINA) {
            SgDetalleRutaTerrestreVo detalleRutaTerrestreVo = sgDetalleRutaTerrestreRemote.buscarDetalleRutaTerrestreDestinoPorRuta(vo.getIdRuta());
            vo.setIdOficinaDestino(detalleRutaTerrestreVo.getIdSgOficina());
            vo.setDestino(detalleRutaTerrestreVo.getNombreSgOficina());
        } else if (vo.getTipoRuta() == Constantes.RUTA_TIPO_CIUDAD) {
            vo.setDestino(sgDetalleRutaCiudadRemote.buscarDetalleRutaCiudadDestinoPorRuta(vo.getIdRuta()).getCiudad());
        } else {
            vo.setIdItinerario(result[40] != null ? (Integer) result[40] : 0);
            vo.setDestino(sgItinerarioRemote.buscarItinerarioCompletoVoPorIdItinerario(vo.getIdItinerario(), false, "").getNombreCiudadDestino());
        }
        if (conViajeros) {
            vo.setListaViajeros(sgViajeroRemote.getTravellersByTravel(vo.getId(), null));
        }
        vo.setIdOpercion(result[30] != null ? (Integer) result[30] : Constantes.CERO);
        //
        vo.setIdOpercionIntercambio(result[31] != null ? (Integer) result[31] : Constantes.CERO);
        // vehiculo
        vo.getVehiculoVO().setId(result[32] != null ? (Integer) result[32] : 0);
        vo.getVehiculoVO().setNumeroPlaca((String) result[33]);
        vo.getVehiculoVO().setMarca((String) result[34]);
        vo.getVehiculoVO().setModelo((String) result[35]);
        vo.getVehiculoVO().setTipoEspecifico((String) result[36]);
        vo.getVehiculoVO().setCapacidadPasajeros(result[39] != null ? (Integer) result[39] : Constantes.CERO);
        vo.getVehiculoVO().setIdOficina(result[42] != null ? (Integer) result[42] : Constantes.CERO);
        vo.setIdViajeVehiculo(result[37] != null ? (Integer) result[37] : 0);
        vo.setIdOpercionRetomarViaje(result[38] != null ? (Integer) result[38] : Constantes.CERO);

        vo.setIdResponsable((String) result[41]);
        if (conNoticia && vo.getIdNoticia() != null && vo.getIdNoticia() > 0) {
            NoticiaVO noticia = new NoticiaVO();
            noticia = coNoticiaService.traerNoticia(vo.getIdNoticia());
            vo.setNoticia(noticia);
        }

        if (detRuta) {
            vo.setLstRutaDet(this.getRutaSectores(vo.getIdRuta()));
        }

        return vo;
    }

    public ViajeVO buscarPorCodigo(String codigo) {
        UtilLog4j.log.info(this, "SgViajeImpl.findByCodigo()");
        clearQuery();
        try {
            String sql = "SELECT V.ID as idViaje,V.codigo as codViaje,V.fecha_programada,V.hora_programada,"
                    + "V.fecha_salida,V.hora_salida,V.fecha_regreso,V.hora_regreso,\n"
                    + "                u.id as idresponsable, u.nombre as responsable,\n"
                    + "                via.id as idViajero, via.estancia,\n"
                    + "                uv.id as idViajero,uv.nombre as viajero,\n"
                    + "                i.id as idInvntado,i.nombre as invitado,\n"
                    + "                s.id as idSolicitud,s.codigo as codSolicitud,\n"
                    + "                veh.id as idVehiculo, veh.numero_placa,\n"
                    + "                mod.nombre AS MODELO, mar.nombre as marca, col.nombre as color,\n"
                    + "                tVeh.id as idTipoEspecifico, tVeh.nombre as tipoEspesifico, veh.capacidad_pasajeros,\n"
                    + "                v.estatus, e.nombre  \n"
                    + "                FROM sg_viaje V\n"
                    + "                INNER JOIN usuario U ON u.id = v.responsable and u.eliminado=false \n"
                    + "                LEFT join sg_viajero via on via.sg_viaje = v.id and via.eliminado=false\n"
                    + "                left join usuario uv on uv.id=via.usuario and uv.eliminado=false\n"
                    + "                left join sg_invitado i on i.id = via.sg_invitado\n"
                    + "                LEFT join sg_solicitud_viaje s on s.id = via.sg_solicitud_viaje and s.eliminado=false\n"
                    + "                INNER join sg_viaje_vehiculo vv on vv.sg_viaje = v.id and vv.eliminado = false\n"
                    + "                inner join sg_vehiculo veh on veh.id = vv.sg_vehiculo\n"
                    + "                inner join sg_modelo mod on mod.id = veh.sg_modelo and mod.eliminado = false\n"
                    + "                inner join sg_marca mar on mar.id = veh.sg_marca and mar.eliminado = false\n"
                    + "                inner join sg_color col on col.id = veh.sg_color and col.eliminado = false\n"
                    + "                inner join sg_tipo_especifico tVeh on tveh.id = veh.sg_tipo_especifico and tVeh.eliminado=False\n"
                    + "                INNER join estatus e on e.id = v.estatus \n"
                    + "                WHERE v.codigo = ?";

            List<Object[]> list = em.createNativeQuery(sql)
                    .setParameter(1, codigo)
                    .getResultList();
            return castConsulta(list);
        } catch (Exception nure) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepcion  al traer el viaje por + + + + + " + nure);
            return null;
        }
    }

    public ViajeVO castConsulta(List<Object[]> l) {
        ViajeVO via = null;
        List<ViajeroVO> viajeros = new ArrayList<>();
        int i = 0;
        if (l != null && !l.isEmpty()) {
            via = new ViajeVO();
            for (Object[] o : l) {
                if (i != (Integer) o[0]) {
                    i = (Integer) o[0];
                    via.setId((Integer) o[0]);
                    via.setCodigo((String) o[1]);
                    via.setFechaProgramada((Date) o[2]);
                    via.setHoraProgramada((Date) o[3]);
                    via.setFechaSalida((Date) o[4]);
                    via.setHoraSalida((Date) o[5]);
                    via.setFechaRegreso((Date) o[6]);
                    via.setHoraRegreso((Date) o[7]);
                    via.setIdResponsable((String) o[8]);
                    via.setResponsable((String) o[9]);
                    via.setIdEstatus((int) o[26]);
                    via.setEstatus((String) o[27]);

                    VehiculoVO vh = new VehiculoVO();
                    vh.setId((Integer) o[18]);
                    vh.setNumeroPlaca((String) o[19]);
                    vh.setModelo((String) o[20]);
                    vh.setMarca((String) o[21]);
                    vh.setColor((String) o[22]);
                    vh.setIdTipoEspecifico((int) o[23]);
                    vh.setTipoEspecifico((String) o[24]);
                    vh.setCapacidadPasajeros((Integer) o[25]);

                    via.setVehiculoVO(vh);
                }
                ViajeroVO v = new ViajeroVO();
                v.setId((Integer) o[10]);
                v.setEstancia((boolean) o[11]);
                v.setIdUsuario((String) o[12]);
                v.setUsuario((String) o[13]);
                v.setIdInvitado((Integer) o[14]);
                v.setInvitado((String) o[15]);
                v.setIdSolicitudViaje((int) o[16]);
                v.setCodigoSolicitudViaje((String) o[17]);
                v.setEmpleado(o[12] != null ? Constantes.TRUE : Constantes.FALSE);
                viajeros.add(v);

            }
            via.setListaViajeros(viajeros);
        }
        return via;
    }

    public ViajeVO buscarPorCodigo(String codigo, boolean conViajeros, boolean conNoticia
    ) {
        UtilLog4j.log.info(this, "SgViajeImpl.findByCodigo()");
        clearQuery();
        try {
            query.append(consultaViaje());
            query.append(" WHERE  v.codigo = '").append(codigo).append("'");
            query.append(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] result = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castViajeBuscar(result, conViajeros, conNoticia, false);
        } catch (Exception nure) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepcion  al traer el viaje por + + + + + " + nure.getMessage());
            return null;
        }
    }

    private String consultaViaje() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT v.id,  v.codigo,  e.nombre, v.fecha_salida,  v.hora_salida,  v.fecha_regreso,"
                //          0         1          2         3                 4              5
                + "  v.hora_regreso, u.nombre,  te.nombre,  "
                //       6             7           8
                + "  v.autobus,  v.vehiculo_propio, v.vehiculo_asignado_empresa, o.nombre,  "
                //     9                10               11                            12
                + "  v.sg_viaje,v.co_noticia,e.id , v.sg_tipo_especifico, v.fecha_programada,  "
                //       13            14     15           16                    17           
                + "  v.hora_programada, v.sg_ruta_terrestre, rt.NOMBRE, v.redondo, v.sg_itinerario, a.ID, a.NOMBRE,"
                //         18                 19                20           21             22       23      24
                + "  a.UUID, rt.sg_tipo_especifico, v.sg_viaje_ciudad, u.telefono, o.id, "
                //      25            26                   27              28       29
                + " (select case when ((select count(id) from GR_INTERSECCION where (SG_VIAJE_A = v.ID or SG_VIAJE_B = v.ID)) = count(vm.ID)) then ").append(Constantes.ID_SI_OP_LLEGO_VIAJE).append(" else 0 end "
                + "		 from SG_VIAJE_SI_MOVIMIENTO vm \n"
                + "				    inner join SI_MOVIMIENTO m1 on vm.SI_MOVIMIENTO = m1.ID \n"
                + "				where vm.SG_VIAJE = v.ID "
                + "				and m1.SI_OPERACION = ").append(Constantes.ID_SI_OP_LLEGO_VIAJE).append(
                "				and vm.ELIMINADO = 'False'), "
                //30
                + " (select case when ((select count(id) from GR_INTERSECCION where (SG_VIAJE_A = v.ID or SG_VIAJE_B = v.ID)) = count(vm.ID)) then ").append(Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE).append(" else 0 end "
                + "		 from SG_VIAJE_SI_MOVIMIENTO vm \n"
                + "				    inner join SI_MOVIMIENTO m1 on vm.SI_MOVIMIENTO = m1.ID \n"
                + "				where vm.SG_VIAJE = v.ID "
                + "				and m1.SI_OPERACION = ").append(Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE).append(
                "				and vm.ELIMINADO = 'False') "
                + "  , vi.ID, vi.NUMERO_PLACA, mar.NOMBRE, mo.nombre, tev.NOMBRE, vv.id, "
                //31      32          33           34            35        36        37
                + " (select case when ((select count(id) from GR_INTERSECCION where (SG_VIAJE_A = v.ID or SG_VIAJE_B = v.ID)) = count(vm.ID)) then ").append(Constantes.ID_SI_OPERACION_RETOMAR_VIAJE).append(" else 0 end "
                + "		from SG_VIAJE_SI_MOVIMIENTO vm \n"
                + "				    inner join SI_MOVIMIENTO m1 on vm.SI_MOVIMIENTO = m1.ID \n"
                + "				where vm.SG_VIAJE = v.ID "
                + "				and m1.SI_OPERACION = ").append(Constantes.ID_SI_OPERACION_RETOMAR_VIAJE).append(
                "				and vm.ELIMINADO = 'False'),  vi.CAPACIDAD_PASAJEROS, v.sg_itinerario, u.id, vi.SG_OFICINA, v.conchofer, v.sg_viaje "
                //                                                  38              39                     40         41         42           43              44                                
                + " FROM SG_VIAJE v"
                + "      inner join ESTATUS e on v.ESTATUS = e.ID "
                + "      inner join USUARIO u on v.RESPONSABLE = u.ID "
                + "      inner join SG_OFICINA o on v.SG_OFICINA = o.ID"
                + "      inner join SG_tipo_especifico te on v.SG_TIPO_ESPECIFICO = te.ID"
                + "      left join si_adjunto a on v.SI_ADJUNTO = a.ID"
                + "      left join co_noticia co on v.CO_NOTICIA = co.ID"
                + "      left join SG_RUTA_TERRESTRE rt on v.SG_RUTA_TERRESTRE = rt.ID"
                + "      left join SG_VIAJE_VEHICULO vv on vv.SG_VIAJE = v.ID"
                + "      left join SG_VEHICULO  vi on vv.SG_VEHICULO = vi.ID"
                + "      left join SG_MARCA mar on vi.SG_MARCA = mar.ID"
                + "      left join SG_MODELO mo on vi.SG_MODELO = mo.id"
                + "      left join SG_TIPO_ESPECIFICO tev on vi.SG_TIPO_ESPECIFICO = tev.ID");
        return sb.toString();
    }

    public List<ViajeVO> traerViajesAereoPorEstatus(int status, boolean conViajeros) {
        UtilLog4j.log.info(this, "getAllTripsByStatus");
        List<ViajeVO> lv = null;
        clearQuery();
        appendQuery("SELECT ");
        appendQuery("v.id, "); //0
        appendQuery("v.codigo, "); //1
        appendQuery("e.nombre AS nombre_estatus,"); //2
        appendQuery("v.fecha_salida, "); //3
        appendQuery("v.hora_salida, "); //4
        appendQuery("v.fecha_regreso,"); //5
        appendQuery("v.hora_regreso,"); //6
        appendQuery("u.nombre as responsable, "); //7
        appendQuery("te.nombre as tipo_especifico, "); //8
        appendQuery("v.autobus, "); //9
        appendQuery("v.vehiculo_propio, "); //10
        appendQuery("v.vehiculo_asignado_empresa,"); //11
        appendQuery("o.nombre AS nombre_oficina, "); //12
        appendQuery("CASE WHEN v.sg_viaje is null THEN 0 "); //13
        appendQuery("WHEN v.sg_viaje is not null THEN 1 "); //13
        appendQuery("END AS isViajeIda, ");//13
        appendQuery("CASE WHEN v.co_noticia is null THEN 0 ");
        appendQuery("WHEN v.co_noticia is not null THEN v.co_noticia ");//14
        appendQuery("END AS co_noticia,");
        appendQuery(" CASE WHEN (SELECT m.sg_viaje FROM sg_viaje_si_movimiento m where m.sg_viaje = v.id AND eliminado='False') is null then 'False' ");//15
        appendQuery("      WHEN (SELECT m.sg_viaje FROM sg_viaje_si_movimiento m where m.sg_viaje = v.id AND eliminado='False') is not null THEN 'True' end as cancelado, ");
        appendQuery(" e.id, ");
        appendQuery(" v.sg_tipo_especifico, ");
        appendQuery("v.fecha_programada, "); //3
        appendQuery("v.hora_programada, "); //4
        appendQuery(" case when v.sg_ruta_terrestre is null then 0 else v.sg_ruta_terrestre end, ");
        appendQuery(" v.redondo, v.sg_itinerario, 0 ");
        //
        appendQuery(" FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te ");
        //
        appendQuery(" where v.ESTATUS = ").append(status).append(" and v.FECHA_PROGRAMADA = (SELECT CURRENT_DATE + INTERVAL '1 day')");
        appendQuery(" and v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_AEREA);
        appendQuery(" AND v.estatus = e.id");
        appendQuery(" AND v.sg_tipo_especifico = te.id");
        appendQuery(" and v.SG_OFICINA = o.ID  and v.RESPONSABLE = u.ID ");
        appendQuery(" and v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'").append(" order by o.nombre asc ");

        //
        UtilLog4j.log.info(this, "Recupera los viajes por salir " + query.toString());
        //
        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

        if (l != null) {
            lv = new ArrayList<ViajeVO>();
            for (Object[] objects : l) {
                lv.add(castViajeBuscar(objects, conViajeros, false, false));
            }
        }
        return lv;
    }

    private String traerCorreo(List<UsuarioRolVo> lrol) {
        String cc = "";
        for (UsuarioRolVo usuario1 : lrol) {
            if (cc.isEmpty()) {
                cc = usuario1.getCorreo();
            } else {
                cc += ", " + usuario1.getCorreo();
            }
        }
        return cc;
    }

    public List<ViajeVO> traerViajesPorSolicitud(int idSolicitud) {
        try {
            UtilLog4j.log.info(this, "traerViajesPorSolicitud");
            List<ViajeVO> lv = null;
            //
            String sql = "select v.ID, v.CODIGO, v.FECHA_PROGRAMADA , v.HORA_PROGRAMADA , v.REDONDO, "
                    + " v.fecha_salida , v.hora_salida ,o.id as idOficinaOrigen, o.nombre as origen \n"
                    + "             from  SG_VIAJE v \n"
                    + "             INNER JOIN SG_VIAJERO vj ON vj.sg_viaje = v.id and vj.eliminado = ? \n"
                    + "             inner join sg_solicitud_viaje s on s.id = vj.sg_solicitud_viaje and s.eliminado = ? \n"
                    + "             inner join sg_oficina o on o.id = v.sg_oficina and o.eliminado= ? \n"
                    + "             where v.eliminado = false and s.id= ? \n"
                    + "             group by v.ID, v.CODIGO, v.FECHA_PROGRAMADA, v.HORA_PROGRAMADA, v.REDONDO, o.id";

            lv = dbCtx.fetch(sql, Constantes.FALSE, Constantes.FALSE, Constantes.FALSE, idSolicitud).into(ViajeVO.class);

//            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
//            if (lo != null) {
//                lv = new ArrayList<ViajeVO>();
//                ViajeVO vo = new ViajeVO();
//                for (Object[] objects : lo) {
//                    vo.setId((Integer) objects[0]);
//                    vo.setCodigo((String) objects[1]);
//                    vo.setFechaProgramada((Date) objects[2]);
//                    vo.setHoraProgramada((Date) objects[3]);
//                    vo.setRedondo((Boolean) objects[4]);
//                    lv.add(vo);
//                }
//            }
            return lv;
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Ocurrio un error al recuperar los viajes por solicitud", e);
            return null;
        }
    }

    public List<ViajeVO> buscarTotalViaje(int idOficina, int anio) {
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            query.append(" select nombre, count(codigo) from (");
            query.append(" select v.CODIGO, g.NOMBRE from SG_VIAJE v ");
            query.append("      inner join SG_VIAJERO vj on vj.SG_VIAJE = v.ID ");
            query.append("      inner join SG_SOLICITUD_VIAJE sv on vj.SG_SOLICITUD_VIAJE = sv.ID");
            query.append("      inner join GERENCIA g on sv.GERENCIA_RESPONSABLE = g.ID");
            query.append("  where v.SG_OFICINA = ").append(idOficina);
            query.append("  and extract(year from v.FECHA_SALIDA) = ").append(anio);
            query.append("  and v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
            query.append("  and v.ESTATUS  <> ").append(Constantes.ESTATUS_VIAJE_CANCELADO);
            query.append("  and v.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  and vj.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  and sv.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  group by v.codigo, g.NOMBRE");
            query.append(" )    group by nombre");
//
            //
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                ViajeVO vo = new ViajeVO();
                vo.setGerencia((String) objects[0]);
                vo.setTotal((Integer) objects[1]);
                lv.add(vo);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar los viajes : : : : :  :" + e.getMessage());
        }
        return lv;
    }

    public List<ViajeVO> traerViajeGerenciaOficina(int idOficina, String gerencia, String inicio, String fin) {
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            clearQuery();
            query.append("select v.codigo, u.NOMBRE, inv.NOMBRE, g.NOMBRE, v.FECHA_SALIDA, v.HORA_SALIDA, v.FECHA_REGRESO, res.nombre, mod.NOMBRE, o.NOMBRE, c.NOMBRE from SG_VIAJE v ");
            query.append("      inner join SG_VIAJERO vj on vj.SG_VIAJE = v.ID");
            query.append("      inner join SG_SOLICITUD_VIAJE sv on vj.SG_SOLICITUD_VIAJE = sv.ID");
            query.append("      inner join GERENCIA g on sv.GERENCIA_RESPONSABLE = g.ID");
            query.append("      left join SG_INVITADO inv on vj.SG_INVITADO = inv.ID");
            query.append("      left join USUARIO u on vj.USUARIO = u.ID");
            query.append("      left join USUARIO res on v.RESPONSABLE = res.ID");
            query.append("      left join SG_RUTA_TERRESTRE rt on v.SG_RUTA_TERRESTRE = rt.ID");
            query.append("      left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = rt.ID and drt.eliminado = 'False'");
            query.append("      left join SG_OFICINA o on  drt.SG_OFICINA = o.ID");
            query.append("      left join SG_VIAJE_CIUDAD vc on vc.SG_SOLICITUD_VIAJE = sv.ID");
            query.append("      left join SI_CIUDAD c on vc.SI_CIUDAD = c.ID");
            query.append("      inner join SG_VIAJE_VEHICULO vv on vv.SG_VIAJE = v.ID");
            query.append("      inner join SG_VEHICULO vh on vv.SG_VEHICULO = vh.ID");
            query.append("      inner join SG_MODELO mod on vh.SG_MODELO = mod.ID");
            query.append("      \n");
            query.append("  where v.SG_OFICINA = ").append(idOficina);
            query.append("   and v.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            query.append("   and g.NOMBRE = '").append(gerencia).append("'");
            query.append("   and v.ESTATUS  <> ").append(Constantes.ESTATUS_VIAJE_CANCELADO);
            query.append("   and v.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("   and vj.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  order by u.nombre asc");
            //
            //
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                ViajeVO vo = new ViajeVO();
                vo.setCodigo((String) objects[0]);
                vo.setViajero((String) objects[1] != null ? (String) objects[1] : (String) objects[2]);
                vo.setGerencia((String) objects[3]);
                vo.setFechaSalida((Date) objects[4]);
                vo.setHoraSalida((Date) objects[5]);
                vo.setFechaRegreso((Date) objects[6]);
                vo.setResponsable((String) objects[7]);
                vo.setVehiculo((String) objects[8]);
                vo.setDestino((String) objects[9] != null ? (String) objects[9] : (String) objects[10]);
                lv.add(vo);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar los viajes : : : : :  :" + e.getMessage());
        }
        return lv;
    }

    public boolean crearViajeAereo(int sgOficina, String usuario, int idSolicitudViaje, String mailUsuario) {
        boolean v = false;
        try {
            List<ViajeroVO> lv = sgViajeroRemote.getAllViajerosList(idSolicitudViaje);
            boolean notificar = false;
            SolicitudViajeVO svvo = sgSolicitudViajeRemote.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO, Constantes.CERO);
            ItinerarioCompletoVo itinerarioIda = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitudViaje, true, true, "id");

            SgViaje sgViaje = new SgViaje();
            //
            sgViaje.setFechaProgramada(itinerarioIda.getEscalas().get(0).getFechaSalida());
            sgViaje.setHoraProgramada(itinerarioIda.getEscalas().get(0).getHoraSalida());
            sgViaje.setFechaSalida(itinerarioIda.getEscalas().get(0).getFechaSalida());
            sgViaje.setHoraSalida(itinerarioIda.getEscalas().get(0).getHoraSalida());
            //
            if (svvo.isRedondo()) {
                sgViaje.setFechaRegreso(svvo.getFechaRegreso());
                sgViaje.setHoraRegreso(svvo.getHoraRegreso());
                sgViaje.setRedondo(Constantes.BOOLEAN_TRUE);
            } else {
                sgViaje.setRedondo(Constantes.BOOLEAN_FALSE);
            }
            sgViaje.setCodigo("VI" + getDigitosAño(new Date()) + "-" + String.valueOf(folioRemote.getFolio(Constantes.CONSECUTIVO_VIAJE)));
            sgViaje.setSgOficina(sgOficinaRemote.find(sgOficina));
            sgViaje.setAutobus(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoPropio(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoAsignadoEmpresa(Constantes.BOOLEAN_FALSE);

            sgViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(3));
            sgViaje.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_POR_SALIR));
            sgViaje.setSgItinerario(sgItinerarioRemote.find(itinerarioIda.getId()));
            sgViaje.setGenero(new Usuario(usuario));
            sgViaje.setFechaGenero(new Date());
            sgViaje.setHoraGenero(new Date());
            sgViaje.setEliminado(Constantes.NO_ELIMINADO);
            sgViaje.setUsuarioRegresaViaje(new Usuario(usuario));
            //
            UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, svvo.getIdGerencia());
            //
            sgViaje.setResponsable(new Usuario(usuarioResponsableGerenciaVo.getIdUsuario()));
            int idViajeIda = sgViaje.getSgViaje() == null ? 0 : sgViaje.getSgViaje().getId();
            v = notificacionViajeRemote.sendMailAirTravel(usuarioResponsableGerenciaVo.getEmailUsuario(),
                    correoCopia(mailUsuario, svvo), svvo, lv,
                    sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(),
                    sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(), sgViaje.isRedondo(),
                    idViajeIda, sgViaje.getCodigo(), usuarioResponsableGerenciaVo.getNombreUsuario());
            if (v) {
                create(sgViaje);
                //Publica noticia
                createEventNews(sgViaje.getId(), usuario);

                for (ViajeroVO Viajero : lv) {
                    SgViajero sgViajero = sgViajeroRemote.find(Viajero.getId());
                    sgViajeroRemote.agregarViaje(usuario, sgViajero, sgViaje, notificar);
                }
                //Finaliza lasolicitus

                sgEstatusAprobacionRemote.finalizeRequest(idSolicitudViaje, new Usuario(usuario), 0);
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exc: guardar viaje aereo: " + e.getMessage() + "  appendQuery(appendQuery(appendQuery(" + e.getCause().toString());
        }
        return v;
    }

    public List<ViajeVO> traerViajesAereosPorFechas(String inicio, String fin) {
        clearQuery();
        query.append("select  v.id, sv.CODIGO as Solicitud , v.CODIGO as Viaje, u.NOMBRE, ori.NOMBRE, ");
        query.append("  des.NOMBRE, v.FECHA_SALIDA, v.HORA_SALIDA, v.FECHA_REGRESO, ");
        query.append("  ( select sum(f.MONTO) from SI_FACTURA f where  f.id in (select vf.SI_FACTURA from SG_VIAJE_FACTURA vf  where vf.SG_VIAJE = v.ID and vf.ELIMINADO = 'False')) ");
        query.append("   from SG_VIAJE v");
        query.append("      inner join SG_ITINERARIO t on v.SG_ITINERARIO = t.ID ");
        query.append("      inner join SG_SOLICITUD_VIAJE sv on t.SG_SOLICITUD_VIAJE = sv.ID ");
        query.append("      inner join SI_CIUDAD ori on t.SI_CIUDAD_ORIGEN = ori.ID ");
        query.append("      inner join SI_CIUDAD des on t.SI_CIUDAD_DESTINO = des.ID ");
        query.append("      inner join USUARIO u on v.RESPONSABLE = u.ID ");
        query.append("  where v.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
        query.append("  order by v.CODIGO asc");
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                lv.add(castGastoViaje(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "#### ocurrió un error al traer los viajes aereos  + + + + + +  " + e.getMessage());
        }

        return lv;
    }

    private ViajeVO castGastoViaje(Object[] objects) {
        ViajeVO v = new ViajeVO();
        v.setId((Integer) objects[0]);
        v.setSolicitud((String) objects[1]);
        v.setCodigo((String) objects[2]);
        v.setResponsable((String) objects[3]);
        v.setOrigen((String) objects[4]);
        v.setDestino((String) objects[5]);
        v.setFechaSalida((Date) objects[6]);
        v.setHoraSalida((Date) objects[7]);
        v.setFechaRegreso((Date) objects[8]);
        v.setGasto(objects[9] != null ? (Double) objects[9] : 0);
        return v;
    }

    public List<ViajeVO> viajeTerrestre(int status, String fecha) {
        UtilLog4j.log.info(this, "SgViajeImpl.viajeTerrestre()");

        List<ViajeVO> list = new ArrayList<ViajeVO>();
        clearQuery();
        try {
            //                     0       1       2        3            4             5                      6                     7                8               9
            appendQuery("SELECT  v.id, v.codigo,  e.id,  e.nombre,  v.autobus, v.vehiculo_propio, v.vehiculo_asignado_empresa, v.responsable,  v.fecha_salida, v.hora_salida,  ");
            //(                  10                 11         12              13
            appendQuery("  v.fecha_regreso, v.hora_regreso, v.sg_oficina,  v.sg_viaje, ");
            //                    14            15       16           17             18
            appendQuery("  v.SG_VIAJE_CIUDAD, rt.id, rt.nombre,  v.sg_oficina, v.si_adjunto, ");
            //                19          20            21
            appendQuery("  u.nombre, v.redondo, v.fecha_programada, ");
            //                    22           23    24  25 26  27 28  29        
            appendQuery("  v.hora_programada, a.uuid, 0, '','', '', '', ''");
            appendQuery(" FROM SG_VIAJE v ");
            appendQuery("       inner join sg_ruta_terrestre rt on v.sg_ruta_terrestre = rt.id ");
            appendQuery("       inner join estatus e on v.estatus = e.id ");
            appendQuery("       inner join usuario u on v.responsable = u.id ");
            appendQuery("       left join si_adjunto a on v.si_adjunto = a.id ");
            appendQuery(" WHERE v.estatus =   ").append(status);
            appendQuery(" and v.fecha_programada = cast('").append(fecha).append("' as date)");
            appendQuery(" AND v.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery(" AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            //
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                list.add(castViajePorSalir(objects, false, false));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "EXCEPCION al traer los viajes : : ::  +++" + e);
        }
        UtilLog4j.log.info(this, "Se encontraron " + (list != null && !list.isEmpty() ? list.size() : "0") + " Viajes en Estatus: " + status);
        return (list != null && !list.isEmpty() ? list : Collections.EMPTY_LIST);
    }

    public boolean guardarViajeEmergente(String sesion, String vehiculoEmpresa, Date fechaSalida, int horaSalida, int minutoSalida,
            int idVehiculo, String responsable, int idRuta, int oficina, int tipoViaje, boolean redondo) {
        boolean v = false;
        try {
            SgViaje sgViaje = this.guardarViajeEmergenteVO(sesion, vehiculoEmpresa, fechaSalida, horaSalida, minutoSalida, idVehiculo, responsable,
                    idRuta, oficina, tipoViaje, null, redondo, Constantes.ESTATUS_VIAJE_POR_SALIR, Constantes.BOOLEAN_TRUE, Constantes.BOOLEAN_FALSE);
            if (sgViaje != null && sgViaje.getId() > 0) {
                v = true;
            }
        } catch (Exception e) {
            v = false;
            UtilLog4j.log.fatal(this, e);
        }
        return v;
    }

    public SgViaje guardarViajeEmergenteVO(String sesion, String vehiculoEmpresa, Date fechaSalida, int horaSalida, int minutoSalida,
            int idVehiculo, String responsable, int idRuta, int oficina, int tipoViaje, String autorizo, boolean redondo, int status, boolean conChofer, boolean interception) {
        SgViaje sgViaje = null;
        try {
            sgViaje = new SgViaje();
            sgViaje.setCodigo("VI" + getDigitosAño(new Date()) + "-" + String.valueOf(folioRemote.getFolio(Constantes.CONSECUTIVO_VIAJE)));

            sgViaje.setAutobus(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoPropio(Constantes.BOOLEAN_FALSE);
            sgViaje.setVehiculoAsignadoEmpresa(Constantes.BOOLEAN_TRUE);
            sgViaje.setEstatus(new Estatus(status));
            sgViaje.setFechaProgramada(fechaSalida);

            if (horaSalida > 0 && minutoSalida > 0) {
                sgViaje.setHoraProgramada(siManejoFechaLocal.componerHora(horaSalida, minutoSalida));
            } else {
                sgViaje.setHoraProgramada(fechaSalida);
            }

            sgViaje.setRedondo((redondo ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE));
            sgViaje.setResponsable(usuarioRemote.find(responsable));
            sgViaje.setSgOficina(sgOficinaRemote.find(oficina));
            sgViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(idRuta));
            sgViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.SOLICITUDES_TERRESTRE));
            sgViaje.setGenero(new Usuario(sesion));
            sgViaje.setFechaGenero(new Date());
            sgViaje.setHoraGenero(new Date());
            sgViaje.setEliminado(Constantes.NO_ELIMINADO);
            sgViaje.setConChofer(conChofer);
            sgViaje.setConIntercepcion(interception);
            if (autorizo != null && !autorizo.isEmpty()) {
                sgViaje.setAutorizo_eme(usuarioRemote.find(autorizo));
            }
            create(sgViaje);

            //viaje a ciudad
            sgViajeVehiculoRemote.guardarViajeVehiculo(sesion, idVehiculo, sgViaje.getId());

            if (tipoViaje == Constantes.RUTA_TIPO_CIUDAD) {
                SgDetalleRutaTerrestreVo sgDetalleRutaCiudad = sgDetalleRutaCiudadRemote.buscarDetalleRutaCiudadDestinoPorRuta(idRuta);
                SgViajeCiudad viajeCiudad = sgViajeCiudadRemote.crear(sgDetalleRutaCiudad.getIdCiudad(), sesion);
                sgViaje.setSgViajeCiudad(viajeCiudad);
                edit(sgViaje);
            }

            CoNoticia noticia = sgViajeRemote.createEventNews(sgViaje.getId(), sesion);
            if (status == Constantes.ESTATUS_VIAJE_POR_SALIR) {
                coCompartidaRemote.compartir(noticia, siUsuarioRolRemote.traerRolPorCodigo("GRADM", Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_GR));
            }

        } catch (Exception e) {
            sgViaje = null;
            UtilLog4j.log.fatal(this, e);
        }
        return sgViaje;
    }

    public List<ViajeVO> sgviajesXSalir(int oficina) throws SIAException {
        UtilLog4j.log.info(this, "SgViajeImpl.getRoadTripByExit()");

        List<ViajeVO> list = new ArrayList<ViajeVO>();
        clearQuery();
        try {
            QuerysBaseViajes();
            appendQuery(queryBaseViajesSalida);

            appendQuery(" WHERE v.estatus =   ").append(Constantes.ESTATUS_VIAJE_POR_SALIR);
            appendQuery(" and v.SG_RUTA_TERRESTRE = rt.id");
            appendQuery("  AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_tipo_especifico = 2");
            appendQuery(" AND V.FECHA_PROGRAMADA >= cast ('now' as date) ");
            appendQuery(" AND v.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND v.sg_oficina = ").append(oficina);
            appendQuery(" ORDER BY V.FECHA_PROGRAMADA ASC");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                list.add(castViajePorSalir(objects, false, false));
            }
            for (ViajeVO l : list) {
                l.setListaViajeros(sgViajeroRemote.getTravellersByTravel(l.getId(), null));
                l.setOrigen(sgOficinaRemote.find(l.getIdOficinaOrigen()).getNombre());
                if (l.getIdSgViajeCiudad() == 0) {
                    l.setDestino(sgDetalleRutaTerrestreRemote.findSgDetalleRutaTerrestreDestinoBySgRutaTerrestre(
                            l.getIdRuta()).getSgOficina().getNombre());
                } else {
                    l.setDestino(sgDetalleRutaCiudadRemote.buscarDetalleRutaCiudadDestinoPorRuta(l.getIdRuta()).getCiudad());
                }

            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
        }
        UtilLog4j.log.info(this, "Se encontraron " + (list != null && !list.isEmpty() ? list.size() : "0") + " Viajes en Estatus: " + 501);
        return (list != null && !list.isEmpty() ? list : Collections.EMPTY_LIST);
    }

    private void QuerysBaseViajes() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT  v.id, "//0
                + " v.codigo,  "//1
                + "  e.id,  "//2
                + "  e.nombre,  "//3
                + "  v.autobus,  "//4
                + "  v.vehiculo_propio,  "//5
                + "  v.vehiculo_asignado_empresa,  "//6
                + "  v.responsable,  "//7
                + "  v.fecha_salida,  "//8
                + "  v.hora_salida,  "//9
                + "  v.fecha_regreso, "//10
                + "  v.hora_regreso, "//11
                + "  v.sg_oficina,  "//12
                + "  case when v.sg_viaje is null then 0"
                + "       when v.sg_viaje is not null then v.sg_viaje  "
                + "      end, "//13
                + "  case when v.SG_VIAJE_CIUDAD is null then 0"
                + "      else v.SG_VIAJE_CIUDAD "
                + "   end,"//14
                + "  case when rt.id is null then 0"
                + "       when rt.id is not null then rt.id"
                + "      end,"//15
                + "   rt.nombre,  "//16
                + "  case when v.sg_oficina is null then 0"
                + "       else v.sg_oficina"
                + "      end,"//17
                + "  case when v.si_adjunto is null then 0"
                + "       when v.si_adjunto is not null then v.si_adjunto  "
                + "      end,"//18
                + "  u.nombre as responsable,  "//19
                + "  v.redondo,"//20
                + "  v.fecha_programada, "//21
                + "  v.hora_programada, "//22
                + " CASE WHEN v.SI_ADJUNTO is null THEN ''"
                + " WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   "
                + " END AS uuid_adjunto,"//23
                + " ve.CAPACIDAD_PASAJEROS, "//24
                + " CASE WHEN drt.id > 0 then ot.NOMBRE ELSE c.NOMBRE  END,"//25
                + " rt.TIEMPO_VIAJE,"//26 
                + " case WHEN v.CON_INTERCEPCION is not null THEN v.CON_INTERCEPCION else 'False' end,"//27
                + " ori.nombre, "//28
                + " u.telefono,"//29
                + " v.CONCHOFER"//30
                + " FROM SG_VIAJE v "
                + " inner join SG_RUTA_TERRESTRE rt on rt.id = v.SG_RUTA_TERRESTRE and rt.ELIMINADO = 'False'   "
                + " left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = rt.ID and drt.ELIMINADO = 'False'   "
                + " left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = rt.ID and drc.ELIMINADO = 'False'   "
                + " left join SG_OFICINA ot on ot.id = drt.SG_OFICINA  and ot.ELIMINADO = 'False'   "
                + " left join SI_CIUDAD c on c.id = drc.SI_CIUDAD  and c.ELIMINADO = 'False'   "
                + " inner join Estatus e on e.id = v.ESTATUS and e.ELIMINADO = 'False' "
                + " inner join usuario u on u.id = v.RESPONSABLE and u.ELIMINADO = 'False' "
                + " inner join SG_VIAJE_VEHICULO vv on vv.SG_VIAJE = v.id and vv.ELIMINADO = 'False' "
                + " inner join SG_VEHICULO ve on ve.id = vv.SG_VEHICULO  and ve.ELIMINADO = 'False' "
                + " inner join SG_OFICINA ori on ori.id = v.SG_OFICINA  and ori.ELIMINADO = 'False'");

        queryBaseViajesSalida = sb.toString();
        sb = new StringBuilder();

        sb.append("SELECT v.id," //0
                + "v.codigo, " //1
                + "e.nombre, " //2
                + "v.fecha_salida, " //3
                + "v.hora_salida, " //4
                + "v.fecha_regreso," //5
                + "v.hora_regreso," //6
                + "u.nombre as responsable, " //7
                + "te.nombre as tipo, " //8
                + "v.autobus, " //9
                + "v.vehiculo_propio, " //10
                + "v.vehiculo_asignado_empresa," //11
                + "o.nombre," //12
                + "0," //13
                + "'False'," //14
                //  appendQuery("'False'," //15
                + "case when v.sg_viaje is null then 0"
                + "     when v.sg_viaje is not null then v.sg_viaje " //16
                + "    end, "
                + "case when v.si_adjunto is null then 0"
                + "     when v.si_adjunto is not null then v.si_adjunto " //17
                + "    end,"
                + "  v.redondo,"
                + "  v.fecha_programada, "
                + "  v.hora_programada, "
                + "  o.id, "
                + "  case when v.estatus_anterior is null then 0 else v.estatus_anterior end,"
                //
                + " CASE WHEN v.SI_ADJUNTO is null THEN ''"
                + " WHEN v.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = v.si_adjunto)   "
                + " END AS uuid_adjunto"//22
                //
                + " FROM SG_VIAJE v, Estatus e, usuario u, sg_oficina o, sg_tipo_especifico te");

        queryBaseViajesSalidaAereos = sb.toString();
    }

    public List<ViajeVO> viajesBySalirAereos() throws SIAException {
        try {
            clearQuery();
            QuerysBaseViajes();
            appendQuery(queryBaseViajesSalidaAereos);
            appendQuery(" WHERE v.estatus = ").append(Constantes.ESTATUS_VIAJE_POR_SALIR);
            appendQuery(" AND te.id = 3");
            appendQuery(" AND v.estatus = e.id");
            appendQuery(" AND v.responsable = u.id");
            appendQuery(" AND v.sg_oficina = o.id");
            appendQuery(" AND v.sg_tipo_especifico = te.id");
            appendQuery(" AND v.id NOT IN (SELECT m.sg_viaje FROM sg_viaje_si_movimiento m)");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND V.FECHA_PROGRAMADA >= cast ('now' as date)");
//            UtilLog4j.log.info(this, "Recupera los viajes aereos Estatus: " + status + " --> " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<ViajeVO> lv = new ArrayList<ViajeVO>();
            UtilLog4j.log.info(this, "Lo: " + lo.size());
            for (Object[] objects : lo) {
                lv.add(castViajeVO(objects));
            }

            return lv;
        } catch (Exception e) {
            throw new SIAException("Error en algun lado " + e);
        }
    }

    /**
     *
     * @param usuario
     * @param sgViaje
     * @param listaViajero
     * @param vehiculoVO
     * @return
     */
    public boolean validarViajesTerrestres(Usuario usuario, SgViaje sgViaje, List<ViajeroVO> listaViajero, VehiculoVO vehiculoVO) {

        //Variables para los correos
        boolean retorno = false;
        String para = "";
        String correoPara = sgViaje.getResponsable().getEmail();
        String telefono = sgViaje.getResponsable().getTelefono();
        String cc = "";

        //a oficina
        SgViajeCiudad sgViajeCiudad = null;
        if (sgViaje.getSgViajeCiudad() == null) {
            if (!sgViaje.isConChofer()) {
                //SIN CHOFER A OFICINA
                String roles = "" + Constantes.SGL_SEGURIDAD + ", " + Constantes.ROL_CENTRO_OPERACION;
                para = this.correosNotificacionViajePorSalir(sgViaje.getId(), Constantes.AP_CAMPO_NEJO, roles);
                retorno = this.notificacionViajeRemote.sendEmailTravelCompanyCarForGenero(para, correoPara, vehiculoVO.getId(), listaViajero,
                        sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(),
                        sgViaje.getHoraRegreso(), sgViaje.isRedondo(), sgViaje.getSgViaje() != null ? sgViaje.getSgViaje().getId() : 0, sgViaje.getCodigo(),
                        sgViaje.isVehiculoAsignadoEmpresa(), sgViaje.isAutobus(), sgViaje.isVehiculoPropio(),
                        Constantes.RUTA_TIPO_OFICINA, sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(),
                        telefono, "");
            } else {
                //CON CHOFER A OFICINA
                para = this.correosNotificacionViajePorSalir(sgViaje.getId(), Constantes.AP_CAMPO_NEJO, null);
                retorno = this.notificacionViajeRemote.sendEmailTravelCompanyCarForGenero(para, correoPara, vehiculoVO.getId(),
                        listaViajero, sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(),
                        sgViaje.isRedondo(), 0, sgViaje.getCodigo(), sgViaje.isVehiculoAsignadoEmpresa(), sgViaje.isAutobus(), sgViaje.isVehiculoPropio(),
                        Constantes.RUTA_TIPO_OFICINA, sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(),
                        telefono, "");
                //Publicar Viaje
                retorno = notificacionViajeRemote.sendMailPublicTravelCompanyCar(publicaViaje(5, sgViaje.getSgOficina().getId()), sgViaje.getFechaProgramada(),
                        sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(),
                        sgViaje.getHoraRegreso(), sgViaje.getCodigo(), sgViaje.isRedondo(), Constantes.RUTA_TIPO_OFICINA, sgViaje.getSgOficina().getNombre(),
                        sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(), telefono,
                        usuario.getNombre());
            }
        } else {
            //a ciudad
            sgViajeCiudad = sgViaje.getSgViajeCiudad();
            UtilLog4j.log.info(this, "destno " + sgViajeCiudad.getSiCiudad().getNombre());
            sgViaje.setSgViajeCiudad(sgViajeCiudad);
            UtilLog4j.log.info(this, "Correo de publicacion");

            if (!sgViaje.isConChofer()) {
                String roles = "" + Constantes.SGL_SEGURIDAD + ", " + Constantes.ROL_CENTRO_OPERACION;
                para = this.correosNotificacionViajePorSalir(sgViaje.getId(), Constantes.AP_CAMPO_NEJO, roles);
                retorno = this.notificacionViajeRemote.sendEmailTravelCompanyCarForGenero(para, correoPara, vehiculoVO.getId(), listaViajero,
                        sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(),
                        sgViaje.getHoraRegreso(), sgViaje.isRedondo(), sgViaje.getSgViaje() != null ? sgViaje.getSgViaje().getId() : 0, sgViaje.getCodigo(),
                        sgViaje.isVehiculoAsignadoEmpresa(), sgViaje.isAutobus(), sgViaje.isVehiculoPropio(),
                        Constantes.RUTA_TIPO_CIUDAD, sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(),
                        sgViaje.getResponsable().getNombre(), "");
            } else {
                para = this.correosNotificacionViajePorSalir(sgViaje.getId(), Constantes.AP_CAMPO_NEJO, null);
                retorno = this.notificacionViajeRemote.sendEmailTravelCompanyCarForGenero(para, correoPara, vehiculoVO.getId(), listaViajero,
                        sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(), sgViaje.getFechaRegreso(),
                        sgViaje.getHoraRegreso(), sgViaje.isRedondo(), sgViaje.getSgViaje() != null ? sgViaje.getSgViaje().getId() : 0, sgViaje.getCodigo(),
                        sgViaje.isVehiculoAsignadoEmpresa(), sgViaje.isAutobus(), sgViaje.isVehiculoPropio(),
                        Constantes.RUTA_TIPO_CIUDAD, sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(), sgViaje.getResponsable().getNombre(),
                        sgViaje.getResponsable().getNombre(), "");
            }

        }
        return retorno;
    }

    public List<ViajeVO> traerViajesPorResponsable(String responsable, int statusInicio, int statusFin, boolean conViajeros) {
        clearQuery();
        List<ViajeVO> lista = null;
        try {
            query.append(consultaViaje());
            query.append("  WHERE v.responsable  = '").append(responsable).append("'");
            query.append("  and v.estatus in (").append(statusInicio).append(" , ").append(statusFin).append(")");
            query.append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  order by e.id desc");
            List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();
            if (result != null) {
                lista = new ArrayList<ViajeVO>();
                for (Object[] obj : result) {
                    lista.add(castViajeBuscar(obj, conViajeros, false, false));
                }
            }

        } catch (Exception nure) {
            UtilLog4j.log.fatal(this, nure.getMessage());
        }
        return lista;
    }

    public void limpiarViajes(Usuario usuario) {
        try {
            //viajes en bandeja de salida que ya no cumplen las normas
            clearQuery();
            appendQuery("SELECT v.ID,v.CODIGO,v.ESTATUS");
            appendQuery(" FROM SG_VIAJE v ");
            appendQuery(" WHERE v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery("  and ((v.estatus IN (").
                    //append(Constantes.ESTATUS_VIAJE_PROCESO).append(",").
                    append(Constantes.ESTATUS_VIAJE_EN_DESTINO).append(")");
            if (usuario != null && !usuario.getId().isEmpty()) {
                appendQuery(" AND V.USUARIO_REGRESA_VIAJE='").append(usuario.getId()).append("'");
            }
            appendQuery(" AND v.FECHA_SALIDA < cast ('now' as date)");
            appendQuery(" AND (v.FECHA_REGRESO < cast ('now' as date)");
            appendQuery(" or v.FECHA_REGRESO is null))");
            appendQuery(" or (v.estatus IN(").append(Constantes.ESTATUS_VIAJE_POR_SALIR).append(",").append(Constantes.ESTATUS_VIAJE_CREADO).append(")");
            if (usuario != null && usuario.getSgOficina() != null && usuario.getSgOficina().getId() > 0) {
                appendQuery(" AND V.SG_OFICINA = ").append(usuario.getSgOficina().getId());
            }
            appendQuery(" and V.FECHA_PROGRAMADA < cast ('now' as date)))");

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<ViajeVO> lv = new ArrayList<ViajeVO>();
            for (Object[] objects : lo) {
                lv.add(castViajeVOLimpiar(objects));
            }
            if (usuario == null || usuario.getId().isEmpty()) {
                usuario = usuarioRemote.find("SIA");
            }
            for (ViajeVO vo : lv) {
                List<ViajeroVO> listViajeros = sgViajeroRemote.getTravellersByTravel(vo.getId(), null);
                if (listViajeros != null) {

                    if (vo.getIdEstatus() == Constantes.ESTATUS_VIAJE_EN_DESTINO) {
                        for (Iterator<ViajeroVO> vi = listViajeros.iterator(); vi.hasNext();) {
                            ViajeroVO via = vi.next();
                            if (!via.isRedondo()) {
                                vi.remove();
                            }

                        }
                        sgViajeroRemote.dejaUsuarioOficinaDestinoViajeSencillo(listViajeros, usuario.getId());
                    } else if (vo.getIdEstatus() <= Constantes.ESTATUS_VIAJE_CREADO) { // revisar para poder bajar en origen                        
                        for (ViajeroVO viaVo : listViajeros) {
                            SgViajero vro = sgViajeroRemote.find(viaVo.getId());
                            SgViajero vroEscala = sgViajeroRemote.sgViajeroByViajeroEscala(vo.getId(), Constantes.BOOLEAN_TRUE);

                            if (vro != null && vro.getId() > 0 && vro.getSgViajero() == null
                                    && vroEscala == null) {
                                sgViajeroRemote.takeOutTravellToTraveller(usuario, vro, "bajado por falta  de administración del viaje", listViajeros.size(), Constantes.FALSE);
                            } else if (vro != null && vro.getId() > 0 && vro.getSgViajero() != null && vroEscala == null) {
                                vro.setSgViaje(vro.getSgViajero().getSgViaje());
                                vro.setEliminado(Constantes.BOOLEAN_TRUE);
                                sgViajeroRemote.edit(vro);
                            } else if (vro != null && vro.getId() > 0 && vro.getSgViajero() == null && vroEscala != null) {
                                //mismo caso
                                //vro.setSgViaje(vro.getSgViajero().getSgViaje());
                                sgViajeroRemote.bajarViajeroConEscala(vro.getId(), vroEscala.getId(), usuario.getId());
                            }
                        }

                    }

                }

                SgViaje viaje = sgViajeRemote.find(vo.getId());
                viaje.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_FINALIZAR));
                viaje.setModifico(usuario);
                viaje.setFechaModifico(new Date());
                viaje.setHoraModifico(new Date());
                edit(viaje);
                sgViajeSiMovimientoRemote.guardarViajeMovimiento(usuario.getId(), vo.getId(),
                        Constantes.ID_SI_OPERACION_FINALIZAR_VIAJES_FT, "Finaliza los viajes fuera de tiempo por falta de administración del Analista");
            }

        } catch (Exception e) {
            try {
                throw new SIAException("Error: " + e);
            } catch (SIAException ex) {
                Logger.getLogger(SgViajeImpl.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    private ViajeVO castViajeVOLimpiar(Object[] objects) {
        ViajeVO vo = new ViajeVO();
        try {
            vo = new ViajeVO();
            vo.setId((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setIdEstatus((Integer) objects[2]);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al hacer el casting de Viaje");
            UtilLog4j.log.info(this, e.getMessage());
        }
        return vo;
    }

    public void mensajeLlegada(int viaje, String usuarioSesion, int llegoA, String punto) {
        notificacionViajeRemote.mensajeLlegada(buscarPorId(viaje, false), usuarioSesion, llegoA, punto);
    }

    private String correoUsuariosResponsables() {
        StringBuilder correoPara = new StringBuilder();
        List<Integer> li = new ArrayList<Integer>();
        li.add(Constantes.SGL_RESPONSABLE);
        li.add(Constantes.SGL_SEGURIDAD);
        li.add(Constantes.ROL_CENTRO_OPERACION);
        List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);

        int nlist = lu.size();
        int x = 1;

        for (UsuarioRolVo usuario1 : lu) {
            correoPara.append(usuario1.getCorreo());
            if (x < nlist) {
                correoPara.append(", ");
            }
            x++;
        }
        return correoPara.toString();
    }

    public void pasarViajeros(int viajeA, int viajeB, int puntoId, String punto, int idOficinaOrigenViajeA,
            int idOficinaOrigenViajeB, String oficinaOrigenViajeA, String oficinaOrigenViajeB, String usuarioSesion) {
        List<ViajeroVO> viajerosViajeA = sgViajeroRemote.getTravellersByTravel(viajeA, null);
        List<ViajeroVO> viajerosViajeB = sgViajeroRemote.getTravellersByTravel(viajeB, null);
        for (ViajeroVO viajerosA : viajerosViajeA) {
            sgViajeroSiMovimientoRemote.guardaMovimiento(usuarioSesion, viajerosA.getId(), "Cambio de viajeros", Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE);
            sgViajeroRemote.agregarViajeroAViaje(usuarioSesion, viajeB, viajerosA,
                    viajerosA.getTipoViajero(), viajerosA.getIdInvitado(), viajerosA.getInvitado(),
                    "Intercambio de viajeros", Constantes.ID_SI_OPERACION_INTERCAMBIO_VIAJERO);

        }

        for (ViajeroVO viajerosB : viajerosViajeB) {
            sgViajeroSiMovimientoRemote.guardaMovimiento(usuarioSesion, viajerosB.getId(), "Cambio de viajeros", Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE);
            sgViajeroRemote.agregarViajeroAViaje(usuarioSesion, viajeA, viajerosB,
                    viajerosB.getTipoViajero(), viajerosB.getIdInvitado(), viajerosB.getInvitado(),
                    "Intercambio de viajeros", Constantes.ID_SI_OPERACION_INTERCAMBIO_VIAJERO);
        }
        notificacionViajeRemote.mensajeCambioViajeros(punto, buscarPorId(viajeA, false), viajerosViajeA, buscarPorId(viajeB, false), viajerosViajeB);

        //	Guradar el movimiento viaje A
        sgViajeSiMovimientoRemote.guardarViajeMovimiento(usuarioSesion, viajeA, Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE, "Cambio de viajeros");
        //	Guradar el movimiento viaje B
        sgViajeSiMovimientoRemote.guardarViajeMovimiento(usuarioSesion, viajeB, Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE, "Cambio de viajeros");

        //	Cambiar la ruta del viaje A
    }

    public boolean pasarViajeros(GrIntercepcionVO intercepcion, String usuarioSesion) {
        boolean actualizar = false;
        if (intercepcion != null && intercepcion.getViajeA() != null && intercepcion.getViajeB() != null) {
            if (intercepcion.getViajeA().getListaViajeros() != null) {
                for (ViajeroVO viajeroA : intercepcion.getViajeA().getListaViajeros()) {
                    if (viajeroA.isIntercambiarEnViaje()) {
                        sgViajeroSiMovimientoRemote.guardaMovimiento(usuarioSesion, viajeroA.getId(), "Cambio de viajeros", Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE);
                        sgViajeroRemote.agregarViajeroAViaje(usuarioSesion, intercepcion.getViajeB().getId(), viajeroA,
                                viajeroA.getTipoViajero(), viajeroA.getIdInvitado(), viajeroA.getInvitado(),
                                "Intercambio de viajeros", Constantes.ID_SI_OPERACION_INTERCAMBIO_VIAJERO);
                        actualizar = true;
                    }
                }
            }
            if (intercepcion.getViajeB().getListaViajeros() != null) {
                for (ViajeroVO viajeroB : intercepcion.getViajeB().getListaViajeros()) {
                    if (viajeroB.isIntercambiarEnViaje()) {
                        sgViajeroSiMovimientoRemote.guardaMovimiento(usuarioSesion, viajeroB.getId(), "Cambio de viajeros", Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE);
                        sgViajeroRemote.agregarViajeroAViaje(usuarioSesion, intercepcion.getViajeA().getId(), viajeroB,
                                viajeroB.getTipoViajero(), viajeroB.getIdInvitado(), viajeroB.getInvitado(),
                                "Intercambio de viajeros", Constantes.ID_SI_OPERACION_INTERCAMBIO_VIAJERO);
                        actualizar = true;
                    }
                }
            }
            notificacionViajeRemote.mensajeCambioViajeros(intercepcion.getPuntoSeguridadNombre(), buscarPorId(intercepcion.getViajeA().getId(), false), intercepcion.getViajeA().getListaViajeros(),
                    buscarPorId(intercepcion.getViajeB().getId(), false), intercepcion.getViajeB().getListaViajeros());

            //	Guradar el movimiento viaje A
            sgViajeSiMovimientoRemote.guardarViajeMovimiento(usuarioSesion, intercepcion.getViajeA().getId(), Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE, "Cambio de viajeros");
            //	Guradar el movimiento viaje B
            sgViajeSiMovimientoRemote.guardarViajeMovimiento(usuarioSesion, intercepcion.getViajeB().getId(), Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE, "Cambio de viajeros");
        }
        return actualizar;
    }

    public void mensajeSalidaPunto(int idViaje, String usuarioSesion, int intercambio, String punto) {
        notificacionViajeRemote.mensajeSalidaPunto(buscarPorId(idViaje, false), intercambio, punto);
    }

    public SiAdjunto generarDocumentoAutomatico(int viaje) {
        SiAdjunto siAdjunto = null;

        SgViaje sgViajeDoc = sgViajeRemote.find(viaje);

        if (sgViajeDoc != null) {

            File file = new File(siParametroRemote.find(1).getUploadDirectory() + "SGyL/Viajes/" + "Viaje-" + sgViajeDoc.getCodigo() + ".pdf");

            try (OutputStream fileOut = new FileOutputStream(file);) {

                UtilLog4j.log.info(this, "viaje:" + viaje);

                Document document = new Document(PageSize.LETTER, 30, 30, 30, 30);

                PdfWriter writer = PdfWriter.getInstance(document, fileOut); // Code 2
                Rectangle rct = new Rectangle(36, 54, 559, 788);

                //Definimos un nombre y un tamaño para el PageBox los nombres posibles son: “crop”, “trim”, “art” and “bleed”.
                writer.setBoxSize("art", rct);
                HeaderFooter event = new HeaderFooter();
                event.setCompania(companiaRemote.find(Constantes.RFC_IHSA));
                writer.setPageEvent(event);
                document.open();
                agregarMetaDatos(document);
                document.open();
                agregarMetaDatos(document);

                generarDocumentoViaje(document, sgViajeDoc);
                document.close();
                siAdjunto = siAdjuntoRemote.save("Viaje-" + sgViajeDoc.getCodigo() + ".pdf", "SGyL/Viajes/" + "Viaje-" + sgViajeDoc.getCodigo() + ".pdf", "application/pdf", 10, "SIA");
                if (siAdjunto != null) {
                    sgViajeRemote.addFile(sgViajeDoc, usuarioRemote.find("SIA"), siAdjunto);
                }
            } catch (IOException | DocumentException | SIAException e) {
                UtilLog4j.log.fatal(this, e);
                siAdjunto = null;
            }

        }

        return siAdjunto;
    }

    public void salidaViajeAereo(String usuario, ViajeVO viajeVO, int status) throws SIAException {

        UtilLog4j.log.info(this, "exitTrip aereo");
        SgViaje sgViaje = find(viajeVO.getId());
        sgViaje.setFechaSalida(new Date());
        sgViaje.setHoraSalida(new Date());
        sgViaje.setEstatus(new Estatus(status));
        sgViaje.setModifico(new Usuario(usuario));
        sgViaje.setFechaModifico(new Date());
        sgViaje.setHoraModifico(new Date());
        edit(sgViaje);
    }

    static class HeaderFooter extends PdfPageEventHelper {

        Compania compania;

        public void setCompania(Compania compania) {
            this.compania = compania;
        }
      
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getBoxSize("art");
            //Pie
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(compania.getCalle() + " Col. " + compania.getColonia() + " C.P. " + compania.getCp() + " " + compania.getCiudad() + " "
                            + compania.getEstado() + " Tel. " + compania.getTelefono(), new Font(Font.FontFamily.TIMES_ROMAN, 7, Font.ITALIC)),
                    (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);
        }
    }

    private void agregarMetaDatos(Document document) {
        document.addTitle("Documentos generados automaticos por el SIA");
        document.addSubject("Diferentes tipos");
        document.addAuthor("Marino Luis");
    }

    private void generarDocumentoViaje(Document document, SgViaje sgViaje) throws DocumentException, SIAException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));

            //Crea el encabezado
            document.add(encabezado());
            PdfPCell pCell;
            PdfPTable table;
            Paragraph p = new Paragraph("Logística para el viaje programado el día " + sdf.format(sgViaje.getFechaProgramada()).concat("."),
                    new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            pCell.setPaddingBottom(12);
            table = new PdfPTable(1);
            table.addCell(pCell);
            document.add(table);

            //Datos del viaje
            table = new PdfPTable(1);
            table.getDefaultCell().setBorder(0);
            table.addCell(agregarLineasEnBlanco()); //Linea en blanco
            p = new Paragraph("Datos del viaje", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(pCell);

            PdfPTable t = new PdfPTable(2);
            float[] medidaCeldaViaje = {1.2f, 3.2f};
            t.setWidths(medidaCeldaViaje);
            t.getDefaultCell().setBorder(0);
            t.getDefaultCell().setPaddingLeft(0.5f);
            p = new Paragraph("Fecha salida:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            t.addCell(pCell);

            p = new Paragraph(Constantes.FMT_ddMMyyy.format(sgViaje.getFechaProgramada()), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            t.addCell(pCell);
            //
            p = new Paragraph("Hora salida:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            t.addCell(pCell);
            p = new Paragraph(Constantes.FMT_hmm_a.format(sgViaje.getHoraProgramada()), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            t.addCell(pCell);
            //
            if (sgViaje.getSgViaje() == null) { //Mostrar solo si es un viaje de ida
                p = new Paragraph("Fecha regreso:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                t.addCell(pCell);
                p = new Paragraph(sgViaje.getFechaRegreso() != null ? Constantes.FMT_ddMMyyy.format(sgViaje.getFechaRegreso()) : "-", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                t.addCell(pCell);
                //
                p = new Paragraph("Hora de regreso :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                t.addCell(pCell);
                p = new Paragraph(sgViaje.getHoraRegreso() != null ? Constantes.FMT_hmm_a.format(sgViaje.getHoraRegreso()) : "-", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                t.addCell(pCell);
            }
            table.addCell(t);
            //
            document.add(table);

            //Ruta
            PdfPTable tRuta = new PdfPTable(1);
            if (sgViaje.getSgRutaTerrestre() != null) {

                tRuta.getDefaultCell().setBorder(0);
                tRuta.setSpacingAfter(2.0f);
                tRuta.addCell(agregarLineasEnBlanco()); //Linea en blanco
                PdfPTable ta;
                //Ruta
                p = new Paragraph("Ruta", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tRuta.addCell(pCell);
                List<SgDetalleRutaTerrestre> ldr = sgDetalleRutaTerrestreRemote.getDetailByRuote(sgViaje.getSgRutaTerrestre().getId(), Constantes.NO_ELIMINADO);
                UtilLog4j.log.info(this, "lista detalle ruta: " + ldr.size());
                //
                PdfPTable tabla3 = new PdfPTable(ldr.size() + 1);
                tabla3.getDefaultCell().setBorder(0);
                ta = new PdfPTable(1);
                ta.getDefaultCell().setBorder(0);
                p = new Paragraph("Origen", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                ta.addCell(pCell);
                //
                p = new Paragraph(sgViaje.getSgOficina().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                ta.addCell(pCell);
//        pCell.addElement(ta);
                //
                p = new Paragraph(sgViaje.getSgOficina().getSgDireccion().getSiCiudad().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                ta.addCell(pCell);
                //Origen
                tabla3.addCell(ta);
                for (SgDetalleRutaTerrestre sgDet : ldr) {
                    t = new PdfPTable(1);
                    t.getDefaultCell().setBorder(0);
                    //
                    if (sgDet.isDestino()) {
//                ta = new PdfPTable(1);
                        p = new Paragraph("Destino", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
                        pCell = new PdfPCell(p);
                        pCell.setBorder(0);
                        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        t.addCell(pCell);
                    } else {
//                ta = new PdfPTable(1);
                        p = new Paragraph("De paso", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                        pCell = new PdfPCell(p);
                        pCell.setBorder(0);
                        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        t.addCell(pCell);
                    }
                    p = new Paragraph(sgDet.getSgOficina().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                    pCell = new PdfPCell(p);
                    pCell.setBorder(0);
                    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    t.addCell(pCell);
                    //
                    p = new Paragraph(sgDet.getSgOficina().getSgDireccion().getSiCiudad().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                    pCell = new PdfPCell(p);
                    pCell.setBorder(0);
                    pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    t.addCell(pCell);
                    tabla3.getDefaultCell().setPaddingBottom(2.0f);
                    tabla3.addCell(t);
                }
                tRuta.addCell(tabla3);
                document.add(tRuta);

            } else if (sgViaje.getSgViajeCiudad() != null) {
                tRuta = new PdfPTable(2);
                //Headers para el Origen y el Destino
                float[] medidaCeldas = {2.70f, 2.70f};
                tRuta.setWidths(medidaCeldas);

                PdfPTable header1 = new PdfPTable(1);
                p = new Paragraph("Origen", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                pCell.setPaddingBottom(5);
                header1.addCell(pCell);
                pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tRuta.addCell(header1);

                PdfPTable header2 = new PdfPTable(1);
                p = new Paragraph("Destino", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                pCell.setPaddingBottom(5);
                header2.addCell(pCell);
                pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tRuta.addCell(header2);
                document.add(tRuta);

                //Tabla de datos Origen y destino
                PdfPTable todc = new PdfPTable(2);
                PdfPCell pCellodc;
                Paragraph podc;
                float[] medidaCeldasodc = {2.70f, 2.70f};
                t.setWidths(medidaCeldasodc);

                //Columna 1
                PdfPTable columna1odc = new PdfPTable(1);
                podc = new Paragraph(sgViaje.getSgViaje() == null ? sgViaje.getSgOficina().getNombre() : sgViaje.getSgViajeCiudad().getSiCiudad().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCellodc = new PdfPCell(podc);
                pCellodc.setHorizontalAlignment(Element.ALIGN_LEFT);
                pCellodc.setPaddingBottom(5);
                pCellodc.setBorder(0);
                columna1odc.addCell(pCellodc);
                todc.addCell(columna1odc);

                //Columna 2
                PdfPTable columna2odc = new PdfPTable(1);
                podc = new Paragraph(sgViaje.getSgViaje() == null ? sgViaje.getSgViajeCiudad().getSiCiudad().getNombre() : sgViaje.getSgOficina().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCellodc = new PdfPCell(podc);
                pCellodc.setBorder(0);
                pCellodc.setHorizontalAlignment(Element.ALIGN_LEFT);
                pCellodc.setPaddingBottom(5);
                columna2odc.addCell(pCellodc);
                todc.addCell(columna2odc);

                document.add(todc);
            }

            //Viajeros
            PdfPTable tViajero = new PdfPTable(1);
            tViajero.getDefaultCell().setBorder(0);
            tViajero.setSpacingAfter(2.0f);
            tViajero.addCell(agregarLineasEnBlanco()); //Linea en blanco
            p = new Paragraph("Viajeros", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);

            pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tViajero.addCell(pCell);
            List<ViajeroVO> lv = sgViajeroRemote.getTravellersByTravel(sgViaje.getId(), null);
            UtilLog4j.log.info(this, "Lista viajeros: " + lv.size());
            float[] medidaCeldasViajero = {2.0f, 5.25f};
            //Encabeazado
            t = new PdfPTable(2);
            t.getDefaultCell().setBorder(0);
            t.setWidths(medidaCeldasViajero);
            t.getDefaultCell().setPaddingLeft(0.5f);
            p = new Paragraph("Nombre", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setBackgroundColor(BaseColor.GRAY);
            t.addCell(pCell);

            p = new Paragraph("Motivo de viaje", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setBackgroundColor(BaseColor.GRAY);
            t.addCell(pCell);
            tViajero.addCell(t);
            for (ViajeroVO viajero : lv) {
                t = new PdfPTable(2);
                t.getDefaultCell().setBorder(0);
                t.setWidths(medidaCeldasViajero);
                t.getDefaultCell().setPaddingLeft(0.5f);
                p = new Paragraph(!viajero.getUsuario().equals("null") ? viajero.getUsuario() : viajero.getInvitado(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                t.addCell(pCell);
                if (viajero.getCodigoSolicitudViaje() != null && !viajero.getCodigoSolicitudViaje().equals("")) {
                    p = new Paragraph(sgSolicitudViajeRemote.buscarPorCodigo(viajero.getCodigoSolicitudViaje(), Constantes.NO_ELIMINADO)
                            .get(Constantes.CERO).getMotivo(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                } else {
                    p = new Paragraph("Viajero emergente", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
                }
                pCell = new PdfPCell(p);
                pCell.setBorder(0);
                t.addCell(pCell);
                tViajero.addCell(t);
            }
            tViajero.getDefaultCell().setPaddingBottom(2.0f);
            document.add(tViajero);
            //DAtos del vehiculo
            SgViajeVehiculo sgViajeVehiculo = sgViajeVehiculoRemote.getVehicleByTravel(sgViaje.getId());
            if (sgViajeVehiculo != null) {
                document.add(datosVehiculo(sgViajeVehiculo.getSgVehiculo()));
            }

//COnductor
            PdfPTable tableCon = new PdfPTable(1);
            tableCon.getDefaultCell().setBorder(0);
            tableCon.addCell(agregarLineasEnBlanco()); //Linea en blanco
            tRuta.setSpacingAfter(2.0f);
            p = new Paragraph("Responsable", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            tableCon.addCell(pCell);
//
            t = new PdfPTable(2);
            t.getDefaultCell().setPaddingLeft(0.5f);
            p = new Paragraph("Nombre", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setBackgroundColor(BaseColor.GRAY);
            pCell.setPaddingLeft(0.5f);
            t.addCell(pCell);

            p = new Paragraph("Teléfono", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
            pCell = new PdfPCell(p);
            pCell.setBackgroundColor(BaseColor.GRAY);
            pCell.setBorder(0);
            t.addCell(pCell);
            //Fin encabezado
            t.getDefaultCell().setPaddingLeft(0.5f);
            p = new Paragraph(sgViaje.getResponsable().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            pCell.setPaddingLeft(0.5f);
            t.addCell(pCell);

            p = new Paragraph(sgViaje.getResponsable().getCelular(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
            pCell = new PdfPCell(p);
            pCell.setBorder(0);
            t.addCell(pCell);
            tableCon.addCell(t);
            document.add(tableCon);
            //Firmas

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al crear el documento de viaje : : : : " + e.getMessage());
            UtilLog4j.log.fatal(e);
        }
    }

    private PdfPTable encabezado() {
        try {
            // Create a 3-column table.
            PdfPTable ta = new PdfPTable(3);
            float[] medidaCeldas = {1.30f, 2.30f, 1.30f};
            ta.setWidths(medidaCeldas);
            ta.setWidthPercentage(100);
            PdfPCell pCe = new PdfPCell(Image.getInstance(companiaRemote.find(Constantes.RFC_IHSA).getLogo()));
            pCe.setBorder(0);
            pCe.setHorizontalAlignment(Element.ALIGN_CENTER);
            pCe.setPaddingBottom(12);
            ta.addCell(pCe);
            pCe = new PdfPCell(new Paragraph("Iberoamericana de Hidrocarburos S.A de C.V.",
                    new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            pCe.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            pCe.setPaddingBottom(12);
            pCe.setBorder(0);
            ta.addCell(pCe);
            pCe = new PdfPCell(Image.getInstance(companiaRemote.find("IHI070320FI3").getLogoEsr()));
            pCe.setHorizontalAlignment(Element.ALIGN_CENTER);
            pCe.setPaddingBottom(5);
            pCe.setBorder(0);
            ta.addCell(pCe);
            return ta;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "ocurrio un error en el encabezado : : : : " + e.getMessage());
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    private PdfPCell agregarLineasEnBlanco() {
        PdfPCell pCell;
        pCell = new PdfPCell(new Paragraph());
        pCell.setBorder(0);
        return pCell;
    }

    private PdfPTable datosVehiculo(SgVehiculo sgVehiculo) {
        float[] medidaCeldas = {0.45f, 2.25f};

        PdfPCell pCell;
        pCell = new PdfPCell();
        pCell.setBorder(0);
//        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        pCell.setPaddingBottom(12);
        PdfPTable table = new PdfPTable(1);
        table.getDefaultCell().setBorder(0);
        //Datos del vehiculo

        Paragraph p = new Paragraph("Datos del vehículo", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(pCell);
        PdfPTable t = new PdfPTable(2);
        t.getDefaultCell().setBorder(0);
        t.getDefaultCell().setPaddingLeft(0.5f);
        try {
            t.setWidths(medidaCeldas);
        } catch (DocumentException ex) {
            UtilLog4j.log.fatal(this, "Ocurrio un error en las medidas de ls celdas " + ex.getMessage());
        }
//        t.setWidthPercentage(50);
        p = new Paragraph("Modelo :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(pCell);
        p = new Paragraph(sgVehiculo.getSgModelo().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(pCell);
        //
        p = new Paragraph("Marca:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(pCell);
        p = new Paragraph(sgVehiculo.getSgMarca().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        t.addCell(pCell);
        ///

        p = new Paragraph("Placa :", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(pCell);
        p = new Paragraph(sgVehiculo.getNumeroPlaca(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(pCell);
        //
        p = new Paragraph("Color:", new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(pCell);
        p = new Paragraph(sgVehiculo.getSgColor().getNombre(), new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL));
        pCell = new PdfPCell(p);
        pCell.setBorder(0);
        pCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        t.addCell(pCell);
        table.addCell(t);
        return table;
    }

    public void compartirNoticiaGerentes(Usuario sesion, SgViaje viaje) {
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        try {
            clearQuery();
            query.append(" select ugr.ID ");
            query.append(" from SG_VIAJE a ");
            query.append(" INNER JOIN SG_VIAJERO v on v.SG_VIAJE = a.id and v.USUARIO is not null and v.ELIMINADO = 'False' ");
            query.append(" inner join USUARIO uv on uv.id = v.USUARIO and uv.ELIMINADO = 'False' ");
            query.append(" inner join AP_CAMPO_GERENCIA ug on ug.GERENCIA = uv.GERENCIA and ug.AP_CAMPO = uv.AP_CAMPO and ug.ELIMINADO = 'False' ");
            query.append(" inner join USUARIO ugr on ugr.ID = ug.RESPONSABLE and ugr.ELIMINADO = 'False' ");
            query.append(" where a.ID = ").append(viaje.getId());
            query.append(" and a.ELIMINADO = 'False' ");
            query.append(" group by ugr.ID ");
            List<Object> listObject = em.createNativeQuery(query.toString()).getResultList();
            for (Object objects : listObject) {
                if (coCompartidaRemote.addCompartido(viaje.getCoNoticia().getId(), (String) objects)) {
                    coCompartidaRemote.compartir(viaje.getCoNoticia(), usuarioRemote.find((String) objects), sesion);
                }
                coCompartidaRemote.compartir(viaje.getCoNoticia(), usuarioRemote.find((String) objects), sesion);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "e : " + e.getMessage());
        }
    }

    public boolean tieneRegreso(int idViaje) {
        boolean v = false;
        clearQuery();
        appendQuery("SELECT count(v.id)");
        appendQuery(" FROM SG_VIAJE v");
        appendQuery(" WHERE v.ELIMINADO='False' and v.SG_VIAJE = ").append(idViaje);
        appendQuery(" AND v.ESTATUS NOT IN (").append(Constantes.ESTATUS_VIAJE_CANCELADO).append(", ").append(Constantes.ESTATUS_VIAJE_FINALIZAR).append(")");
        long contador = ((Long) em.createNativeQuery(query.toString()).getSingleResult());
        if (contador > 0) {
            v = true;
        }
        return v;
    }

    public void guardarMovimiento(String usuarioID, int viajeID, int operacionID, String motivo) {
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        try {
            sgViajeSiMovimientoRemote.guardarViajeMovimiento(usuarioID, viajeID, operacionID, motivo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "e : " + e.getMessage());
        }
    }

    public boolean moverViajeAProgramado(String usuarioID, int viajeID) {
        boolean valRet = false;
        boolean validarFechas = false;
        try {
            ViajeVO vo = this.buscarPorId(viajeID, true);
            if (vo != null && vo.getId() > 0) {
                if (vo.getFechaProgramada().after(new Date())) {
                    validarFechas = true;
                } else if (siManejoFechaLocal.dayIsToday(vo.getFechaProgramada())) {
                    validarFechas = true;
                }

                if (vo.getVehiculoVO() != null && vo.getVehiculoVO().getId() > 0 && validarFechas) {
                    this.cambiarEstado(viajeID, usuarioID, Constantes.ESTATUS_VIAJE_POR_SALIR);
                    if (this.validarViajesTerrestres(usuarioRemote.find(usuarioID), this.find(vo.getId()), vo.getListaViajeros(), vo.getVehiculoVO())) {
                        valRet = true;
                    }
                } else if (!validarFechas) {
                    valRet = false;
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return valRet;
    }

    public String correosNotificacionViajePorSalir(int viajeID, int apCampoID, String roles) {
        String correos = "";
        try {
            clearQuery();
            query.append(" select array_to_string(array_agg(EMAIL), ',') from ("
                    + " select ug.EMAIL "
                    + " from SG_VIAJERO a "
                    + " inner join USUARIO u on u.id = a.USUARIO and u.ELIMINADO = 'False' "
                    + " inner join AP_CAMPO_GERENCIA ag on ag.GERENCIA = u.GERENCIA and ag.ELIMINADO = 'False' and ag.AP_CAMPO = ").append(apCampoID).append(
                    " inner join USUARIO ug on ug.id = ag.RESPONSABLE and ug.ELIMINADO = 'False' "
                    + " where a.SG_VIAJE = ").append(viajeID).append(
                    " union "
                    + " select u.EMAIL "
                    + " from SG_VIAJERO a "
                    + " inner join USUARIO u on u.id = a.USUARIO and u.ELIMINADO = 'False' "
                    + " inner join AP_CAMPO_GERENCIA ag on ag.GERENCIA = u.GERENCIA and ag.ELIMINADO = 'False' and ag.AP_CAMPO = ").append(apCampoID).append(
                    " inner join USUARIO ug on ug.id = ag.RESPONSABLE and ug.ELIMINADO = 'False' "
                    + " where a.SG_VIAJE = ").append(viajeID).append(
                    " union "
                    + " select u.EMAIL "
                    + " from SI_USUARIO_ROL ur "
                    + " inner join USUARIO u on ur.USUARIO = u.id and u.ELIMINADO = 'False'  "
                    + " inner join SI_ROL r on ur.SI_ROL = r.id and r.ELIMINADO = 'False'  "
                    + " where ur.ELIMINADO = 'False'  "
                    + " and ur.ap_campo = ").append(apCampoID);
            if (roles != null && !roles.isEmpty()) {
                query.append(" and ur.SI_ROL in (").append(roles).append(") ");
            } else {
                query.append(" and ur.SI_ROL in (10, 15, 16, 9) ");
            }
            query.append(" ) as correosViajesPorSalir ");

            Object object = em.createNativeQuery(query.toString()).getSingleResult();
            if (object != null) {
                correos = String.valueOf(object);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            correos = "";
        }
        return correos;
    }

    /**
     *
     * @param sesion
     * @param viaje
     * @param motivo
     * @param nombreSesion
     */
    public void cancelarViajeAereo(String sesion, int viaje, String motivo, String nombreSesion) {
        SgViaje sgViaje = find(viaje);
        sgViaje.setEstatus(new Estatus(500));
        sgViaje.setModifico(new Usuario(sesion));
        sgViaje.setFechaModifico(new Date());
        sgViaje.setHoraModifico(new Date());
        edit(sgViaje);
        //Guarda en relacion
        sgViajeSiMovimientoRemote.guardarViajeMovimiento(sesion, viaje, Constantes.ID_SI_OPERACION_CANCELAR, motivo);
        //
        notificacionViajeRemote.sendMailCancelTripNoCompanyCar(nombreSesion, "", "", find(viaje), motivo);

        //publicar cancelacio a noticias
        cancelStatusTravelNews(sgViaje.getId(), motivo, sesion);
    }

    public List<GrPuntoVO> getRutaSectores(int idRutaTerrestre) {
        List<GrPuntoVO> list = new ArrayList<GrPuntoVO>();
        clearQuery();
        try {
            appendQuery(" select NOMBRE, SECUENCIA, MAPA, PUNTO from (  "
                    + " select NOMBRE, SECUENCIA, MAPA, PUNTO from (  "
                    + " select m.NOMBRE, cast(a.SECUENCIA as integer) as SECUENCIA, m.ID as MAPA, p.ID as PUNTO     "
                    + " from GR_RUTAS_ZONAS a    "
                    + " inner join GR_MAPA m on m.id = a.GR_MAPA and m.ELIMINADO = 'False'    "
                    + " left join GR_PUNTO p on p.id = a.GR_PUNTO and p.ELIMINADO = 'False'    "
                    + " where a.ELIMINADO = 'False'    "
                    + " and a.SG_RUTA_TERRESTRE = ").append(idRutaTerrestre).append(
                    " order by cast(a.SECUENCIA as integer) limit 1) as zr  "
                    + " union    "
                    + " select p.NOMBRE, cast(a.SECUENCIA as integer) as SECUENCIA, m.ID as MAPA, p.ID as PUNTO    "
                    + " from GR_RUTAS_ZONAS a    "
                    + " inner join GR_MAPA m on m.id = a.GR_MAPA and m.ELIMINADO = 'False'    "
                    + " inner join GR_PUNTO p on p.id = a.GR_PUNTO and p.ELIMINADO = 'False'    "
                    + " where a.ELIMINADO = 'False'    "
                    + " and a.SG_RUTA_TERRESTRE = ").append(idRutaTerrestre).append(
                    " union  "
                    + " select NOMBRE, SECUENCIA, MAPA, PUNTO from (  "
                    + " select m.NOMBRE, cast(a.SECUENCIA as integer) as SECUENCIA, m.ID as MAPA, p.ID as PUNTO    "
                    + " from GR_RUTAS_ZONAS a    "
                    + " inner join GR_MAPA m on m.id = a.GR_MAPA and m.ELIMINADO = 'False'    "
                    + " left join GR_PUNTO p on p.id = a.GR_PUNTO and p.ELIMINADO = 'False'    "
                    + " where a.ELIMINADO = 'False'  and a.SG_RUTA_TERRESTRE = ").append(idRutaTerrestre).append(
                    " order by cast(a.SECUENCIA as integer) desc limit 1) as zr) zrt "
                    + " order by SECUENCIA  ");

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            GrPuntoVO vo = null;
            for (Object[] objects : lo) {
                vo = new GrPuntoVO();
                vo.setNombre(String.valueOf(objects[0]));
                vo.setMapa((Integer) objects[2] != null ? (Integer) objects[2] : 0);
                vo.setId((Integer) objects[3] != null ? (Integer) objects[3] : 0);
                list.add(vo);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return list;
    }

    public List<ViajeVO> totalViaje(int oficina, int anio) {
        List<ViajeVO> lv = new ArrayList<ViajeVO>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select o.NOMBRE as origen, count(v.id) as total from SG_VIAJE v"
                    + " inner join sg_oficina o on v.sg_oficina = o.id"
                    + " where v.ELIMINADO = ").append(Constantes.NO_ELIMINADO)
                    .append(" and v.ESTATUS  <> ").append(Constantes.ESTATUS_VIAJE_CANCELADO)
                    .append(" and v.SG_TIPO_ESPECIFICO = ").append(Constantes.SOLICITUDES_TERRESTRE);

            if (oficina > 0) {
                sb.append("  and v.SG_OFICINA = ").append(oficina);
            }
            if (anio > 0) {
                sb.append("  and extract( year from v.fecha_salida ) = ").append(anio);
            }
            sb.append("  group by o.NOMBRE");
            //
            lv = dbCtx.fetch(sb.toString()).into(ViajeVO.class);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar los viajes : : : : :  :" + e);
        }
        return lv;
    }

    
    public List<ViajeVO> viajesPorAnioMes(int oficina, int anio) {

        List<ViajeVO> lViaje = new ArrayList<ViajeVO>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TO_CHAR(v.Fecha_salida, 'MM') || '/' || TO_CHAR(v.FECHA_SALIDA, 'YYYY') as origen, count(v.ID) as total"
                + " from sg_viaje v"
                + "  where v.SG_OFICINA = ?  and v.ESTATUS <> ?  and v.ELIMINADO = ?");
        if (anio > 0) {
            sql.append(" and extract(year from v.FECHA_SALIDA) = ").append(anio);
        }
        sql.append(" group by TO_CHAR(v.Fecha_salida, 'MM') || '/' || TO_CHAR(v.FECHA_SALIDA, 'YYYY')");
        sql.append(" ORDER by TO_CHAR(v.Fecha_salida, 'MM') || '/' || TO_CHAR(v.FECHA_SALIDA, 'YYYY')");
        
        lViaje = dbCtx.fetch(sql.toString(),oficina,Constantes.ESTATUS_VIAJE_CANCELADO,Constantes.NO_ELIMINADO).into(ViajeVO.class);
                
        return lViaje;
    }

    
    public List<ViajeVO> viajesViajeros(int oficina, String mes) {
        String q = "select v.CODIGO, v.FECHA_SALIDA, v.HORA_SALIDA, u.NOMBRE, uv.NOMBRE, i.NOMBRE, vj.SG_SOLICITUD_VIAJE, vm.nombre, vmod.nombre, vh.numero_placa from SG_VIAJERO vj "
                + "	inner join SG_VIAJE v on vj.SG_VIAJE = v.ID "
                + "	inner join USUARIO u on v.RESPONSABLE = u.ID "
                + "	left  join USUARIO uv on vj.USUARIO = uv.ID "
                + "	left  join SG_INVITADO i on vj.SG_INVITADO = i.ID "
                + "	left  join sg_viaje_vehiculo vv on vv.sg_viaje = v.id "
                + "	inner join sg_vehiculo vh on vv.sg_vehiculo = vh.id "
                + "	inner join sg_marca vm  on vh.sg_marca = vm.id "
                + "	inner join sg_modelo vmod on vh.sg_modelo = vmod.id "
                + " where v.SG_OFICINA = ? "
                + " and to_char(v.FECHA_SALIDA,'MM') || '/' || TO_CHAR(v.FECHA_SALIDA, 'YYYY') = ? "
                + "and v.ESTATUS > 500 "
                + " and vj.ELIMINADO = 'False' "
                + " order by v.codigo asc ";
        
        Query qu = em.createNativeQuery(q);
        qu.setParameter(1, oficina).setParameter(2, mes);
        List<Object[]> lo = qu.getResultList();
        List<ViajeVO> lViaje = new ArrayList<ViajeVO>();
        for (Object[] objects : lo) {
            ViajeVO vo = new ViajeVO();
            vo.setCodigo((String) objects[0]);
            vo.setFechaSalida((Date) objects[1]);
            vo.setHoraSalida((Date) objects[2]);
            vo.setResponsable((String) objects[3]);
            vo.setViajero(objects[4] != null ? (String) objects[4] : (String) objects[5]);
            vo.setSolicitud(objects[6] == null ? "Sin Solicitud" : "Con solicitud");
            vo.setVehiculoVO(new VehiculoVO());
            vo.getVehiculoVO().setMarca((String) objects[7]);
            vo.getVehiculoVO().setModelo((String) objects[8]);
            vo.getVehiculoVO().setNumeroPlaca((String) objects[9]);
            lViaje.add(vo);
        }
        return lViaje;
    }

    
    public void limpiarViajes(int idViaje, String usuario) {
        try {
            //viajes en bandeja de salida que ya no cumplen las normas
            clearQuery();
            appendQuery("SELECT v.ID,v.CODIGO,v.ESTATUS");
            appendQuery(" FROM SG_VIAJE v ");
            appendQuery(" WHERE v.id = '").append(idViaje).append("'");

            Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            ViajeVO vo = new ViajeVO();
            vo = castViajeVOLimpiar(objects);

            List<ViajeroVO> listViajeros = sgViajeroRemote.getTravellersByTravel(vo.getId(), null);

            Usuario u = usuarioRemote.find(usuario);

            if (listViajeros != null) {

                if (vo.getIdEstatus() == Constantes.ESTATUS_VIAJE_EN_DESTINO) {
                    for (ViajeroVO via : listViajeros) {
                        if (via.isRedondo()) {
                            listViajeros.remove(via);
                        }

                    }
                    sgViajeroRemote.dejaUsuarioOficinaDestinoViajeSencillo(listViajeros, usuario);
                } else if (vo.getIdEstatus() <= Constantes.ESTATUS_VIAJE_CREADO) { // revisar para poder bajar en origen
                    for (ViajeroVO viaVo : listViajeros) {
                        SgViajero vro = sgViajeroRemote.find(viaVo.getId());
                        SgViajero vroEscala = sgViajeroRemote.sgViajeroByViajeroEscala(vo.getId(), Constantes.BOOLEAN_TRUE);

                        if (vro != null && vro.getId() > 0 && vro.getSgViajero() == null
                                && vroEscala == null) {
                            sgViajeroRemote.takeOutTravellToTraveller(u, vro, "bajado por falta  de administración del viaje", listViajeros.size(), Constantes.FALSE);
                        } else if (vro != null && vro.getId() > 0 && vro.getSgViajero() != null && vroEscala == null) {
                            vro.setSgViaje(vro.getSgViajero().getSgViaje());
                            vro.setEliminado(Constantes.BOOLEAN_TRUE);
                            sgViajeroRemote.edit(vro);
                        } else if (vro != null && vro.getId() > 0 && vro.getSgViajero() == null && vroEscala != null) {
                            //aplica el mismo caso que al bajarlo manual
                            //vro.setSgViaje(vro.getSgViajero().getSgViaje());
                            vro.setEliminado(Constantes.BOOLEAN_TRUE);
                            sgViajeroRemote.edit(vro);

                            vroEscala.setEliminado(Constantes.BOOLEAN_FALSE);
                            vroEscala.setSgViaje(null);
                            vroEscala.setSgViajero(null);
                            sgViajeroRemote.edit(vroEscala);
                        }
                    }

                }

            }

            SgViaje viaje = sgViajeRemote.find(vo.getId());
            viaje.setEstatus(estatusRemote.find(Constantes.ESTATUS_VIAJE_FINALIZAR));
            viaje.setModifico(u);
            viaje.setFechaModifico(new Date());
            viaje.setHoraModifico(new Date());
            edit(viaje);
            sgViajeSiMovimientoRemote.guardarViajeMovimiento(usuario, vo.getId(),
                    Constantes.ID_SI_OPERACION_FINALIZAR_VIAJES_FT, "Finaliza los viajes fuera de tiempo por falta de administración del Analista");

        } catch (Exception e) {
            try {
                throw new SIAException("Error: " + e);
            } catch (SIAException ex) {
                Logger.getLogger(SgViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
