/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import com.newrelic.api.agent.Trace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.Estatus;
import sia.modelo.SgDetalleSolicitudEstancia;
import sia.modelo.SgMotivo;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SiMovimiento;
import sia.modelo.SiUsuarioCodigo;
import sia.modelo.Usuario;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sgl.semaforo.impl.SgRolApruebaSolicitudImpl;
import sia.servicios.sgl.viaje.impl.SgItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgUbicacionImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.servicios.sistema.impl.SiUsuarioCodigoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;
import sia.util.notificacion.FCMSender;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgSolicitudEstanciaImpl extends AbstractFacade<SgSolicitudEstancia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //
    //
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private EstatusImpl estatusRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    private FolioImpl folioRemote;
    @Inject
    private SgMotivoImpl sgMotivoRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SiMovimientoImpl siMovimientoService;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgTipoImpl sgTipoRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private SgRolApruebaSolicitudImpl sgRolApruebaSolicitudRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private SgItinerarioImpl sgItinerarioRemote;
    @Inject
    private SgSolicitudEstanciaSiMovimientoImpl sgSolicitudEstanciaSiMovimientoRemote;    
    @Inject
    private SiUsuarioCodigoImpl siUsuarioCodigoLocal;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaRemote;
    @Inject 
    private SgUbicacionImpl ubicacionRemote;
    //

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgSolicitudEstanciaImpl() {
        super(SgSolicitudEstancia.class);
    }

    
    public SgSolicitudEstancia guardarSolicitud(Usuario usuario, Date inicio, Date fin, int status, int idOficina,
            boolean eliminado, int idMotivo, int idGerencia,int idubicacion, ApCampo a) {
        UtilLog4j.log.debug(this, "SgSolicitudEstanciaImpl.guardarSolicitud()");
        SgSolicitudEstancia solicitudEstancia = new SgSolicitudEstancia();

        //Recupera el usuario hospeda
        solicitudEstancia.setInicioEstancia(inicio);
        solicitudEstancia.setFinEstancia(fin);
        solicitudEstancia.setDiasEstancia(siManejoFechaLocal.dias(fin, inicio));
        solicitudEstancia.setSgOficina(sgOficinaRemote.find(idOficina));
        solicitudEstancia.setEstatus(estatusRemote.find(status));
        solicitudEstancia.setGerencia(gerenciaRemote.find(idGerencia));
        solicitudEstancia.setGenero(usuario);
        solicitudEstancia.setFechaGenero(new Date());
        solicitudEstancia.setHoraGenero(new Date());
        solicitudEstancia.setEliminado(eliminado);
        solicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
        solicitudEstancia.setSgMotivo(sgMotivoRemote.find(idMotivo));
        solicitudEstancia.setSgUbicacion(ubicacionRemote.find(idubicacion));
        solicitudEstancia.setApCampo(a);
        create(solicitudEstancia);
        return solicitudEstancia;
    }

    private String regresaUsuarioHospeda(int idOficina) {
        String idUsuario;
        if (idOficina == Constantes.ID_OFICINA_TORRE_MARTEL) {
            UsuarioRolVo urv = siUsuarioRolRemote.traerRolUsuarioModulo(Constantes.SGL_RESPONSABLE, 9, Constantes.BOOLEAN_TRUE, Constantes.AP_CAMPO_NEJO);
            idUsuario = urv.getIdUsuario();
        } else if (idOficina == Constantes.ID_OFICINA_REY_INFRA) {
            SgOficinaAnalistaVo oficinaAnalistaVo = sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(Constantes.ID_OFICINA_REY_PRINCIPAL);
            idUsuario = oficinaAnalistaVo.getIdAnalista();
        } else {
            SgOficinaAnalistaVo oficinaAnalistaVo = sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(idOficina);
            idUsuario = oficinaAnalistaVo.getIdAnalista();
        }
        return idUsuario;
    }

    
    public SgSolicitudEstancia guardarSolicitud(String idUsuario, String idUsuarioHospeda, int status, int idOficina, boolean eliminado, int idMotivo, int idGerencia, Date fechaInicio, Date fechaFin) {
        UtilLog4j.log.info(this, "SgSolicitudEstanciaImpl.guardarSolicitud()");
        SgSolicitudEstancia sgSolicitudEstancia = new SgSolicitudEstancia();
        sgSolicitudEstancia.setDiasEstancia(siManejoFechaLocal.dias(fechaFin, fechaInicio));
        sgSolicitudEstancia.setSgOficina(sgOficinaRemote.find(idOficina));
        sgSolicitudEstancia.setEstatus(estatusRemote.find(status));
        sgSolicitudEstancia.setGerencia(gerenciaRemote.find(idGerencia));
        sgSolicitudEstancia.setGenero(new Usuario(idUsuario));
        sgSolicitudEstancia.setInicioEstancia(fechaInicio);
        sgSolicitudEstancia.setFinEstancia(fechaFin);
        sgSolicitudEstancia.setFechaGenero(new Date());
        sgSolicitudEstancia.setHoraGenero(new Date());
        sgSolicitudEstancia.setEliminado(eliminado);
        sgSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
        sgSolicitudEstancia.setSgMotivo(sgMotivoRemote.find(idMotivo));
        sgSolicitudEstancia.setUsuarioHospeda(new Usuario(idUsuarioHospeda));

        create(sgSolicitudEstancia);
        return sgSolicitudEstancia;
    }

    private String getDigitosAño(Date fecha) {
        String Cadena = Constantes.FMT_ddMMyyy.format(fecha);
        String r = Cadena.substring(8, 10);
        return r;
    }

    
    public List<SgSolicitudEstancia> trearSolicitudEstanciaPorUsuario(Usuario usuario, int status, boolean eliminado) {
        try {
            return em.createQuery("SELECT se FROM SgSolicitudEstancia se WHERE se.genero.id = :idGenero"
                    + "  AND se.estatus.id = :estatus "
                    + "  AND se.eliminado = :eli "
                    + "  ORDER BY se.id DESC").setParameter("idGenero", usuario.getId()).setParameter("estatus", status).setParameter("eli", eliminado).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public void modificacionSolicitud(Usuario usuario, int idSilicitudEstancia, Date inicio, Date fin, int idOficina, boolean eliminado, int idMotivo) {
        SgSolicitudEstancia sgSolicitudEstancia = find(idSilicitudEstancia);
        sgSolicitudEstancia.setInicioEstancia(inicio);
        sgSolicitudEstancia.setFinEstancia(fin);
        sgSolicitudEstancia.setGenero(usuario);
        sgSolicitudEstancia.setDiasEstancia(siManejoFechaLocal.dias(sgSolicitudEstancia.getFinEstancia(), sgSolicitudEstancia.getInicioEstancia()));
        sgSolicitudEstancia.setSgMotivo(sgMotivoRemote.find(idMotivo));
        sgSolicitudEstancia.setFechaGenero(new Date());
        sgSolicitudEstancia.setHoraGenero(new Date());
        sgSolicitudEstancia.setEliminado(eliminado);

        edit(sgSolicitudEstancia);
    }

    
    public int totalSgSolicitudEstancia(String idUsuario, int idSgOficina, int idEstatus, Boolean fromTravel) {

        try {
            clearQuery();
            appendQuery("select count(se.ID) "); //0
            appendQuery("from SG_SOLICITUD_ESTANCIA se, SG_OFICINA o, ESTATUS e ");
            appendQuery("where se.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
            if (idEstatus > 0) {
                appendQuery("and se.ESTATUS=").append(idEstatus).append(" ");
            }
            if (idSgOficina > 0) {
                appendQuery("and se.SG_OFICINA=").append(idSgOficina).append(" ");
            }
            if (idUsuario != null && !idUsuario.isEmpty()) {
                appendQuery("and se.GENERO='").append(idUsuario).append("' ");
            }
            appendQuery("and se.SG_OFICINA=o.ID ");
            appendQuery("and o.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
            appendQuery("and se.ESTATUS=e.ID ");
            appendQuery("and e.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
            if (fromTravel != null) {
                appendQuery("and (select count (v.id) from SG_VIAJERO v WHERE v.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ").append("and v.SG_SOLICITUD_ESTANCIA=se.ID)").append(fromTravel ? " > " : " = ").append("0 ");
            }

            Query q = em.createNativeQuery(getStringQuery());

            UtilLog4j.log.debug(this, new StringBuilder().append("Query ").append(q.toString()).toString());

            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.error(this, ("SgSolicitudEstanciaImpl.totalSgSolicitudEstancia()" + e.getMessage()), e);
            return 0;
        }
    }

    
    public boolean solicitarEstancia(SgSolicitudEstanciaVo sgSolicitudEstancia, String sesion, List<DetalleEstanciaVO> detalle) {
        boolean v;
        int status = 0;
        SgSolicitudEstancia solicitudEstancia = find(sgSolicitudEstancia.getId());
        if (solicitudEstancia.getCodigo() == null) {
            sgSolicitudEstancia.setCodigo("SE" + getDigitosAño(new Date()) + "-" + Integer.toString(this.folioRemote.getFolio("SOLICITUD_ESTANCIA_CONSECUTIVO")));
            solicitudEstancia.setCodigo(sgSolicitudEstancia.getCodigo());
        }
        if (usuarioRemote.isGerente(Constantes.AP_CAMPO_DEFAULT, sesion)) {
            v = notificacionServiciosGeneralesRemote.enviarCorreoSolicitaEstancia(Constantes.AP_CAMPO_DEFAULT, sgSolicitudEstancia, detalle);
            status = Constantes.REQUISICION_SOLICITADA;
        } else {
            status = Constantes.REQUISICION_VISTO_BUENO;
            v = notificacionServiciosGeneralesRemote.enviarCorreoAprobarEstancia(sgSolicitudEstancia, detalle);
        }
        solicitudEstancia.setModifico(new Usuario(sesion));
        solicitudEstancia.setFechaModifico(new Date());
        solicitudEstancia.setHoraModifico(new Date());
        solicitudEstancia.setEstatus(new Estatus(status));
        edit(solicitudEstancia);

        return v;
    }

    
    @Trace
    public boolean solicitarEstanciaDeSolicituViaje(int idSolicitudViaje, String idUsuarioHospeda, String idUsuario) {
        UtilLog4j.log.info(this, "solicitarEstancia para un viaje " + idSolicitudViaje);
        SgSolicitudEstancia sgSolicitudEstancia = null;

        boolean v = false;
        try {
            SolicitudViajeVO solicitudVo = sgSolicitudViajeRemote.buscarPorId(idSolicitudViaje, Constantes.BOOLEAN_FALSE, Constantes.CERO);

            if (solicitudVo.getIdSolicitudEstancia() > 0) {
                sgSolicitudEstancia = find(solicitudVo.getIdSolicitudEstancia());

                if (Constantes.USUARIO_PRUEBA.equals(idUsuario)) {
                    sgSolicitudEstancia.setCodigo(generarCodigo(false));
                } else {
                    sgSolicitudEstancia.setCodigo(generarCodigo(true));
                }
                v = notificacionServiciosGeneralesRemote.enviarCorreoSolicitaEstancia(sgSolicitudEstancia.getApCampo().getId(), buscarEstanciaPorId(sgSolicitudEstancia.getId()), sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(sgSolicitudEstancia.getId(), Constantes.NO_ELIMINADO));
                if (v) {
                    String ae = sgSolicitudEstancia.toString();
                    sgSolicitudEstancia.setEstatus(estatusRemote.find(Constantes.ESTATUS_SOLICITUD_ESTANCIA_SOLICITADA));
                    sgSolicitudEstancia.setModifico(new Usuario(idUsuario));
                    sgSolicitudEstancia.setFechaModifico(new Date());
                    sgSolicitudEstancia.setHoraModifico(new Date());
                    edit(sgSolicitudEstancia);                    
                }
            } else {
                UtilLog4j.log.info(this, "//no existen viajeros marcados con estancia, Checar si hay solicitud de estancia creada");
                //no existen viajeros marcados con estancia, Checar si hay solicitud de estancia creada

            }
            return v;
        } catch (Exception ex) {
            Logger.getLogger(SgSolicitudEstanciaImpl.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }
    }

    /**
     * Genera el Código para una Solicitud de Estancia
     *
     * @param isReal 'true' si el código es para una Solicitud de Estancia de
     * Producción, caso contrario, generará el código para una Solicitud de
     * Estncia de Pruebas
     * @return
     *
     */
    private String generarCodigo(boolean isReal) {
        if (isReal) {
            return new StringBuilder().append(Constantes.PREFIJO_SOLICITUD_ESTANCIA_REAL).append(getDigitosAño(new Date())).append(Constantes.GUION).append(Integer.toString(this.folioRemote.getFolio(Constantes.FOLIO_SOLICITUD_ESTANCIA_REAL))).toString();
        } else {
            return new StringBuilder().append(Constantes.PREFIJO_SOLICITUD_ESTANCIA_PRUEBA).append(getDigitosAño(new Date())).append(Constantes.GUION).append(Integer.toString(this.folioRemote.getFolio(Constantes.FOLIO_SOLICITUD_ESTANCIA_PRUEBA))).toString();
        }
    }

    
    public List<SgSolicitudEstanciaVo> trearSolicitudEstanciaPorOficina(int oficina, int status, String idSesion, boolean eliminado) {
        try {
            clearQuery();
            query.append("SELECT s.id,g.id, u.id, m.id,s.CODIGO, g.nombre, s.INICIO_ESTANCIA, s.FIN_ESTANCIA, u.nombre, m.nombre, o.id, o.nombre,");
            query.append(" u.email");
            query.append(" FROM SG_SOLICITUD_ESTANCIA s, GERENCIA g, USUARIO u, SG_MOTIVO m, sg_oficina o");
            query.append(" WHERE ");
            query.append("s.sg_Oficina = ").append(oficina);
            query.append(" AND s.estatus = ").append(status);
//            query.append(" and s.USUARIO_HOSPEDA = '").append(idSesion).append("'");
            query.append(" AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append(" AND s.cancelado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            query.append(" and s.sg_oficina = o.ID ");
            query.append(" and s.GERENCIA = g.ID ");
            query.append(" and s.GENERO = u.ID ");
            query.append(" and s.SG_MOTIVO = m.id");
            query.append(" ORDER BY s.id ASC");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo != null) {
                List<SgSolicitudEstanciaVo> lsol = new ArrayList<SgSolicitudEstanciaVo>();
                for (Object[] objects : lo) {
                    lsol.add(castEstanciaVO(objects));
                }
                return lsol;
            } else {
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public boolean cancelarSolicitudEstancia(Usuario usuario, SgSolicitudEstanciaVo sgSolicitudEstancia, String mensaje, boolean notificar, boolean enviarCorreo) {
        //Envia correo de cancelacion de solicitud
        boolean v;
        try {
            if(enviarCorreo){
                v = notificacionServiciosGeneralesRemote.enviaCorreoCancelaSolicitudEstancia(usuario, sgSolicitudEstancia, mensaje, notificar);
            } else {
                v = Constantes.TRUE;
            }
            
            if (v) {
                //Marcar como Cancelados a los Integrantes de la Solicitud de Estancia
                //Se registra el cambio
                SgSolicitudEstancia solicitudEstancia = find(sgSolicitudEstancia.getId());

                UtilLog4j.log.info(this, "Estancia id: " + solicitudEstancia.getId());
                solicitudEstancia.setModifico(usuario);
                solicitudEstancia.setFechaModifico(new Date());
                solicitudEstancia.setHoraModifico(new Date());
                solicitudEstancia.setCancelado(Constantes.BOOLEAN_TRUE);
                solicitudEstancia.setEstatus(estatusRemote.find(50));
                edit(solicitudEstancia);
                //enviar a si movimiento
                SiMovimiento simo = this.siMovimientoService.save(mensaje, 3, usuario.getId());
                if (simo != null) {
                    UtilLog4j.log.info(this, "Simo.getid" + simo.getId());
                    UtilLog4j.log.info(this, "usuario " + usuario.getId());
                    sgSolicitudEstanciaSiMovimientoRemote.guardarSiMovimiento(solicitudEstancia.getId(), simo.getId(), usuario.getId());
                }
                UtilLog4j.log.info(this, "Despues de guardar el id " + solicitudEstancia.getId());
            }
        } catch (Exception e) {
            v = false;

            UtilLog4j.log.error(this, e.getMessage(), e);
        }
        return v;
    }

    
    public SgSolicitudEstancia buscarEstanciaPorcodigo(String codigo) {
        try {
            return (SgSolicitudEstancia) em.createQuery("SELECT e FROM SgSolicitudEstancia  e WHERE e.codigo = :codigo").setParameter("codigo", codigo).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public boolean buscarMotivoUsado(SgMotivo sgMotivo) {
        try {
            List<SgSolicitudEstancia> l = em.createQuery("SELECT e FROM SgSolicitudEstancia  e WHERE e.sgMotivo.id = :idMotivo").setParameter("idMotivo", sgMotivo.getId()).getResultList();
            if (l.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return false;
        }
    }

    
    public void guardarSolicitudEstancia(String sesion, String idUsuarioAlta, int idGerencia, int idOficina, Date fechaIngreso, Date fechaSalida) {
        SgSolicitudEstancia sgSolicitudEstancia = new SgSolicitudEstancia();
        //
        sgSolicitudEstancia.setCodigo("SE" + getDigitosAño(new Date()) + "-" + Integer.toString(this.folioRemote.getFolio("SOLICITUD_ESTANCIA_CONSECUTIVO")));
        sgSolicitudEstancia.setInicioEstancia(fechaIngreso);
        sgSolicitudEstancia.setFinEstancia(fechaSalida);
        sgSolicitudEstancia.setDiasEstancia(siManejoFechaLocal.dias(fechaSalida, fechaIngreso));
        sgSolicitudEstancia.setSgOficina(sgOficinaRemote.find(idOficina));
        sgSolicitudEstancia.setEstatus(estatusRemote.find(1));
        sgSolicitudEstancia.setGerencia(gerenciaRemote.find(idGerencia));
        sgSolicitudEstancia.setGenero(new Usuario(sesion));
        sgSolicitudEstancia.setFechaGenero(new Date());
        sgSolicitudEstancia.setHoraGenero(new Date());
        sgSolicitudEstancia.setEliminado(Constantes.NO_ELIMINADO);
        sgSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
        sgSolicitudEstancia.setSgMotivo(sgMotivoRemote.find(48));
        create(sgSolicitudEstancia);
        //
        //Agregar detalle a la solicitud
        SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia = new SgDetalleSolicitudEstancia();
        sgDetalleSolicitudEstancia.setUsuario(new Usuario(idUsuarioAlta));
        sgDetalleSolicitudEstancia.setSgInvitado(null);
        sgDetalleSolicitudEstancia.setSgTipoEspecifico(sgTipoEspecificoRemote.find(19));

        sgDetalleSolicitudEstancia.setSgTipo(sgTipoRemote.find(4));
        sgDetalleSolicitudEstancia.setSgSolicitudEstancia(sgSolicitudEstancia);
        sgDetalleSolicitudEstancia.setGenero(new Usuario(sesion));
        sgDetalleSolicitudEstancia.setFechaGenero(new Date());
        sgDetalleSolicitudEstancia.setHoraGenero(new Date());
        sgDetalleSolicitudEstancia.setDescripcion("");
        sgDetalleSolicitudEstancia.setEliminado(Constantes.NO_ELIMINADO);
        sgDetalleSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
        sgDetalleSolicitudEstancia.setRegistrado(Constantes.BOOLEAN_FALSE);
        sgDetalleSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
        sgDetalleSolicitudEstanciaRemote.create(sgDetalleSolicitudEstancia);
        //envia la solicitud de estancia

        solicitarEstancia(buscarEstanciaPorId(sgSolicitudEstancia.getId()), sesion, sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(sgSolicitudEstancia.getId(), Constantes.NO_ELIMINADO));
    }

    
    public List<SgSolicitudEstanciaVo> findAll(String idUsuario, int idSgOficina, int idEstatus, Boolean fromTravel, String orderByField, boolean sortAscending, boolean eliminado) {

        clearQuery();

        appendQuery("select se.ID, "); //0
        appendQuery("se.CODIGO, "); //1
        appendQuery("se.INICIO_ESTANCIA, "); //2
        appendQuery("se.FIN_ESTANCIA, "); //3
        appendQuery("se.DIAS_ESTANCIA, "); //4
        appendQuery("se.SG_OFICINA as idSgOficina, "); //5
        appendQuery("o.NOMBRE as nombreSgOficina, "); //6
        appendQuery("se.ESTATUS as idEstatus, "); //7
        appendQuery("e.NOMBRE as nombreEstatus, "); //8
        appendQuery("g.ID as idGerencia, "); //9
        appendQuery("g.NOMBRE as nombreGerencia, "); //10
        appendQuery("se.SG_MOTIVO as idSgMotivo, "); //11
        appendQuery("m.NOMBRE as nombreSgMotivo, "); //12
        appendQuery("(select count (v.id) from SG_VIAJERO v WHERE v.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ").append("and v.SG_SOLICITUD_ESTANCIA=se.ID) as countViajeros, "); //13
        appendQuery("se.FECHA_GENERO, "); //14
        appendQuery("(select count(dse.ID) from SG_DETALLE_SOLICITUD_ESTANCIA dse where dse.SG_SOLICITUD_ESTANCIA=se.ID and dse.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ").append(") as integrantes  "); //15
        appendQuery("from SG_SOLICITUD_ESTANCIA se, SG_OFICINA o, SG_MOTIVO m, ESTATUS e, GERENCIA g ");
        appendQuery("where se.ELIMINADO='").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");
        if (idEstatus > 0) {
            appendQuery("and se.ESTATUS=").append(idEstatus).append(" ");
        }
        if (idSgOficina > 0) {
            appendQuery("and se.SG_OFICINA=").append(idSgOficina).append(" ");
        }
        if (idUsuario != null && !idUsuario.isEmpty()) {
            appendQuery("and se.GENERO='").append(idUsuario).append("' ");
        }
        appendQuery("and se.SG_OFICINA=o.ID ");
        appendQuery("and o.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.SG_MOTIVO=m.ID ");
        appendQuery("and m.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.ESTATUS=e.ID ");
        appendQuery("and e.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.GERENCIA=g.ID ");
        appendQuery("and g.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");

        if (orderByField != null && !orderByField.isEmpty()) {
            appendQuery("ORDER BY se.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        UtilLog4j.log.debug(this, new StringBuilder().append("Query: ").append(getStringQuery()).toString());

        List<Object[]> result = em.createNativeQuery(getStringQuery()).getResultList();
        List<SgSolicitudEstanciaVo> list = new ArrayList<SgSolicitudEstanciaVo>();
        SgSolicitudEstanciaVo vo;

        for (Object[] objects : result) {
            vo = new SgSolicitudEstanciaVo();
            vo.setId((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setInicioEstancia((Date) objects[2]);
            vo.setFinEstancia((Date) objects[3]);
            vo.setDiasEstancia((Integer) objects[4]);
            vo.setIdSgOfina((Integer) objects[5]);
            vo.setNombreSgOficina((String) objects[6]);
            vo.setIdEstatus((Integer) objects[7]);
            vo.setNombreEstatus((String) objects[8]);
            vo.setIdGerencia((Integer) objects[9]);
            vo.setNombreGerencia((String) objects[10]);
            vo.setIdSgMotivo((Integer) objects[11]);
            vo.setNombreSgMotivo((String) objects[12]);
            vo.setPorViaje(((Integer) objects[13]) > 0);
            vo.setFechaGenero((Date) objects[14]);
            vo.setContIntegrantes((Integer) objects[15]);

            if (fromTravel != null) {
                if (fromTravel && vo.isPorViaje()) {
                    list.add(vo);
                } else if (!fromTravel && !vo.isPorViaje()) {
                    list.add(vo);
                }
            } else {
                list.add(vo);
            }
        }

        UtilLog4j.log.debug(this, new StringBuilder().append("Se encontraron ").append(list.isEmpty() ? "0" : list.size()).append(" SgSolicitudEstancia").toString());

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    @Trace
    public boolean solicitarSolicitudEstanciaCreadaPorSolcicitudViajePendienteDeSolicitar(SgSolicitudViaje sgSolicitudViaje, String idUsuario) {
        log("SgSolicitudEstanciaImpl.enviarSolicitudEstanciaCreadaPorSolcicitudViajePendienteDeSolicitar()");
//        String idUsuarioHospeda = "";

        boolean r = false;
        try {
            if (sgSolicitudViaje != null) {
                List<ViajeroVO> lis = sgViajeroRemote.getViajerosWithEstanciaBySolicitudViajeList(sgSolicitudViaje.getId(), Constantes.BOOLEAN_TRUE);
                if (lis != null && !lis.isEmpty()) {
//                    idUsuarioHospeda = obtenerUsuarioHospeda(sgSolicitudViaje.getGenero().getId(),
//                            sgSolicitudViaje.getId(),
//                            sgSolicitudViaje.getSgTipoSolicitudViaje().getSgTipoEspecifico().getId(),
//                            sgSolicitudViaje.getOficinaOrigen().getId(),
//                            sgSolicitudViaje.getOficinaDestino() != null ? sgSolicitudViaje.getOficinaDestino().getId() : Constantes.CERO,
//                            true);
//                    if (!idUsuarioHospeda.equals("")) {
                    r = solicitarEstanciaDeSolicituViaje(sgSolicitudViaje.getId(), "", idUsuario);
//                    } else {
//                        log("NO EXISTE UN USUARIO PARA HOSPEDAR LA ESTANCIA -- - - - -");
//                        r = false;
//                    }
                } else {
                    log("No existen viajeros marcados con estancia");
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepocion al solicitar las estancias creacdar a partir de la solicitud e viaje " + e.getMessage());
            r = false;
        }

        return r;
    }

    private String obtenerUsuarioHospeda(String idUsuarioGenero, int idSolicitud, int idTipoEspecifico, int idOficinaOrigen, int idOficinaDestino, boolean isViaje) {
        String idUsuarioHospeda = "";
        if (this.sgRolApruebaSolicitudRemote.verificarUsuarioAutoApruebaSolicitudViaje(idUsuarioGenero)) {
            //el que genera ve su solicitud
            idUsuarioHospeda = idUsuarioGenero;
            log(" El usuario que genero la solicitud es el que hospedara la estancia " + idUsuarioHospeda);
        } else {//saber si es terrestre o aerea
            if (isViaje) {
                switch (idTipoEspecifico) {
                    case Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE: {
                        log("Es un viaje Terrestre Oficina a Oficina");
                        //saber si es a ciudad o a oficina
                        if (idOficinaDestino != Constantes.CERO) {
                            idUsuarioHospeda = regresaUsuarioHospeda(idOficinaDestino);
                            log("Es un viaje Terrestre de oficina a oficina ");
                        } else {
                            log("Es un viaje Terrestre de oficina a Ciudad ");
                            idUsuarioHospeda = regresaUsuarioHospeda(idOficinaOrigen);
//                            E
                        }
                        break;
                    }

                    case Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA: {
                        log("Es un viaje aereo - se tomara al usuario que puso el itinerario ");
                        //buscar quien genero el itinerario
                        ItinerarioCompletoVo vo = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitud, true, true, "id");
                        if (vo.getEscalas() != null) {
                            idUsuarioHospeda = vo.getEscalas().get(0).getGenero();
                            log("Usuario que puso el itinerario es " + idUsuarioHospeda);
                        }
                        break;
                    }
                }
            } else {
                log("Es una estancia normal- - - - - se toma");
                /**
                 * ***********Es una estancia normal.**********
                 */
                if (idOficinaDestino == Constantes.ID_OFICINA_TORRE_MARTEL) {
                    idUsuarioHospeda = siUsuarioRolRemote.traerRolUsuarioModulo(Constantes.SGL_RESPONSABLE, Constantes.MODULO_SGYL, Constantes.BOOLEAN_TRUE, Constantes.AP_CAMPO_NEJO).getIdUsuario();
                    log(" la oficin ade origen es torre martel tomar al Responsable de SGL");
                } else {
                    SgOficinaAnalistaVo oficinaAnalistaVo = sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(idOficinaDestino);
                    idUsuarioHospeda = oficinaAnalistaVo.getIdAnalista();
                    log(" la oficina de origen NO es torre martel tomar al analista de la oficina destino");
                }

            }
        }
        return idUsuarioHospeda;
    }

    
    public List<SgSolicitudEstanciaVo> traerTodasSolicitudesEstanciasCreadasPorSolicitudViajePendienteSolicitar(int idSgSolicitudViaje) {

        clearQuery();

        appendQuery("select se.ID, "); //0
        appendQuery("se.CODIGO, "); //1
        appendQuery("se.INICIO_ESTANCIA, "); //2
        appendQuery("se.FIN_ESTANCIA, "); //3
        appendQuery("se.DIAS_ESTANCIA, "); //4
        appendQuery("se.SG_OFICINA as idSgOficina, "); //5
        appendQuery("o.NOMBRE as nombreSgOficina, "); //6
        appendQuery("se.ESTATUS as idEstatus, "); //7
        appendQuery("e.NOMBRE as nombreEstatus, "); //8
        appendQuery("g.ID as idGerencia, "); //9
        appendQuery("g.NOMBRE as nombreGerencia, "); //10
        appendQuery("se.SG_MOTIVO as idSgMotivo, "); //11
        appendQuery("m.NOMBRE as nombreSgMotivo, "); //12
        appendQuery("se.FECHA_GENERO, "); //13
        appendQuery("se.GENERO "); //14
        appendQuery("from SG_SOLICITUD_ESTANCIA se, SG_OFICINA o, SG_MOTIVO m, ESTATUS e, GERENCIA g ");
        appendQuery("where se.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.SG_OFICINA=o.ID ");
        appendQuery("and o.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.SG_MOTIVO=m.ID ");
        appendQuery("and m.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.ESTATUS=e.ID ");
        appendQuery("and e.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.GERENCIA=g.ID ");
        appendQuery("and g.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and se.ID in ( ");
        appendQuery("select distinct(vi.SG_SOLICITUD_ESTANCIA) ");
        appendQuery("from sg_viajero vi, SG_SOLICITUD_VIAJE sv, SG_TIPO_SOLICITUD_VIAJE tsv ");
        appendQuery("where vi.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and vi.SG_SOLICITUD_VIAJE=").append(idSgSolicitudViaje).append(" ");
        appendQuery("and sv.ID=vi.SG_SOLICITUD_VIAJE ");
        appendQuery("and sv.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and sv.ESTATUS <>  ").append(Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO).append(" ");
        //appendQuery("and sv.OFICINA_DESTINO is not null  ");
        appendQuery("and tsv.ID=sv.SG_TIPO_SOLICITUD_VIAJE ");
        appendQuery("and tsv.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("and tsv.SG_TIPO_ESPECIFICO=").append(Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE).append(") ");

        UtilLog4j.log.debug(this, new StringBuilder().append("Query: ").append(query.toString()).toString());

        List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();
        List<SgSolicitudEstanciaVo> list = new ArrayList<SgSolicitudEstanciaVo>();
        SgSolicitudEstanciaVo vo;

        for (Object[] objects : result) {
            vo = new SgSolicitudEstanciaVo();
            vo.setId((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setInicioEstancia((Date) objects[2]);
            vo.setFinEstancia((Date) objects[3]);
            vo.setDiasEstancia((Integer) objects[4]);
            vo.setIdSgOfina((Integer) objects[5]);
            vo.setNombreSgOficina((String) objects[6]);
            vo.setIdEstatus((Integer) objects[7]);
            vo.setNombreEstatus((String) objects[8]);
            vo.setIdGerencia((Integer) objects[9]);
            vo.setNombreGerencia((String) objects[10]);
            vo.setIdSgMotivo((Integer) objects[11]);
            vo.setNombreSgMotivo((String) objects[12]);
            vo.setFechaGenero((Date) objects[13]);
            vo.setGenero((String) objects[14]);

            list.add(vo);
        }
        UtilLog4j.log.debug(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgSolicitudEstancia");

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public List<SgSolicitudEstanciaVo> traerSolicitudesEstanciaGeneradasDeSolicutdViajePorOficinaYRuta(int idOficina, int idRuta) {
        UtilLog4j.log.info(this, "traerSolicitudesEstanciaGeneradasDeSolicutdViajePorOficinaYRuta " + idOficina + " " + idRuta);
        List<SgSolicitudEstanciaVo> list = null;
        try {
            clearQuery();

            appendQuery(" Select est.ID,");//0
            appendQuery(" est.CODIGO, ");//1
            appendQuery(" est.INICIO_ESTANCIA,");//2
            appendQuery(" est.FIN_ESTANCIA,");//3
            appendQuery(" est.DIAS_ESTANCIA,");//4
            appendQuery(" est.ESTATUS,");//5
            appendQuery(" est.GERENCIA,");//6
            appendQuery(" est.SG_MOTIVO,");//7
            appendQuery(" est.SG_OFICINA,");//8
            appendQuery(" est.GENERO");//9
            appendQuery(" from SG_SOLICITUD_ESTANCIA est");
            appendQuery(" where est.id ");
            appendQuery(" in  ");
            appendQuery(" (Select distinct (via.SG_SOLICITUD_ESTANCIA)");
            appendQuery(" From SG_VIAJERO via , SG_SOLICITUD_ESTANCIA soles");
            appendQuery(" where via.ESTANCIA = 'True'");
            appendQuery(" and soles.SG_OFICINA = ");
            appendQuery(idOficina);
            appendQuery(" and via.SG_SOLICITUD_ESTANCIA = soles.ID");
            appendQuery(" and via.ELIMINADO = 'False'");
            appendQuery(" and via.id not in (Select mov.SG_VIAJERO");
            appendQuery("       From SG_VIAJERO_SI_MOVIMIENTO mov, ");
            appendQuery("           SI_MOVIMIENTO simo,");
            appendQuery("           SI_OPERACION op");
            appendQuery("       Where mov.SI_MOVIMIENTO = simo.id ");
            appendQuery("           and op.ID = 3"); // 3 representa el concepto Cancelado en la tabla si_movimiento
            appendQuery("           and simo.SI_OPERACION = op.id");
            appendQuery("           and mov.ELIMINADO = 'False'");
            appendQuery("           and simo.ELIMINADO = 'False')");
            appendQuery(" and via.SG_SOLICITUD_VIAJE in (Select sol.id");
            appendQuery("                   From SG_SOLICITUD_VIAJE sol,");
            appendQuery("                       SG_RUTA_TERRESTRE ruta");
            appendQuery("                   Where sol.ESTATUS between ");
            appendQuery(Constantes.ESTATUS_VISTO_BUENO);
            appendQuery(" and ");
            appendQuery(Constantes.ESTATUS_PARA_HACER_VIAJE);
            appendQuery("                       and ruta.ID = ");
            appendQuery(idRuta);
            appendQuery("                       and ruta.ID = (select es.SG_RUTA_TERRESTRE from SG_ESTADO_SEMAFORO es where es.id = sol.SG_ESTADO_SEMAFORO and es.eliminado = 'False')");
            appendQuery("                       and sol.ELIMINADO = 'False'))");
            UtilLog4j.log.info(this, "++++++++++++++++++++++++" + getStringQuery());
            List<Object[]> result = em.createNativeQuery(getStringQuery()).getResultList();
            if (result != null && !result.isEmpty()) {
                list = new ArrayList<SgSolicitudEstanciaVo>();
                for (Object[] objects : result) {
                    list.add(getCastSgSolicitudEstanciaVo(objects));
                }
            }

            return list;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exepcion al traerSolicitudesPorOficinaYRuta" + e.getMessage());
            return null;
        }
    }

    private SgSolicitudEstanciaVo getCastSgSolicitudEstanciaVo(Object[] objects) {
        UtilLog4j.log.info(this, "getCastSgSolicitudEstanciaVo");
        SgSolicitudEstanciaVo vo = new SgSolicitudEstanciaVo();
        vo.setId((Integer) objects[0]);
        vo.setCodigo((String) objects[1]);
        vo.setInicioEstancia((Date) objects[2]);
        vo.setFinEstancia((Date) objects[3]);
        vo.setDiasEstancia((Integer) objects[4]);
        //vo.setIdSgOfina((Integer) objects[5]);
        //vo.setNombreSgOficina((String) objects[6]);
        vo.setIdEstatus((Integer) objects[5]);
        //vo.setNombreEstatus((String) objects[8]);
        vo.setIdGerencia((Integer) objects[6]);
        //vo.setNombreGerencia((String) objects[10]);
        vo.setIdSgMotivo((Integer) objects[7]);
        //vo.setNombreSgMotivo((String) objects[12]);
        vo.setIdSgOfina((Integer) objects[8]);
        vo.setGenero((String) objects[9]);
        return vo;
    }

    
    public SemaforoVo traerSemaforoActualSolicitudViajeApartirDeSolicitudEstancia(int idSolicitudEstancia) {
        SemaforoVo vo = null;
        try {
            clearQuery();
            appendQuery(" select sem.id,");
            appendQuery(" sem.color,");
            appendQuery(" sem.descripcion");
            appendQuery(" from SG_ESTADO_SEMAFORO es, SG_SEMAFORO sem");
            appendQuery(" where es.SG_RUTA_TERRESTRE =(select es.SG_RUTA_TERRESTRE");
            appendQuery("                                         from SG_SOLICITUD_VIAJE sol,SG_ESTADO_SEMAFORO es");
            appendQuery("                                         where sol.id = (Select distinct (via.SG_SOLICITUD_VIAJE)");
            appendQuery("                                                         from SG_VIAJERO via");
            appendQuery("                                                         where via.SG_SOLICITUD_ESTANCIA = (select e.id from SG_SOLICITUD_ESTANCIA e where e.id = ");
            appendQuery(idSolicitudEstancia);
            appendQuery(" ))");
            appendQuery("                                                         and sol.SG_ESTADO_SEMAFORO = es.ID)");
            appendQuery(" and es.ACTUAL = 'True'");
            appendQuery(" and es.ELIMINADO = 'False' ");
            appendQuery(" and es.SG_SEMAFORO = sem.id");

            Object[] ob = (Object[]) em.createNativeQuery(getStringQuery()).getSingleResult();

            if (ob != null) {
                vo = new SemaforoVo();
                vo.setIdSemaforo((Integer) ob[0]);
                vo.setColor((String) ob[1]);
                vo.setDescripcion((String) ob[2]);
                return vo;
            }
            return vo;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer semaforo actual " + e.getMessage());
            return null;
        }
    }
//s.id,g.id, u.id, m.id,s.CODIGO, g.nombre, s.INICIO_ESTANCIA, s.FIN_ESTANCIA, u.nombre, m.nombre

    private SgSolicitudEstanciaVo castEstanciaVO(Object[] objects) {
        SgSolicitudEstanciaVo solicitudEstanciaVo = new SgSolicitudEstanciaVo();
        solicitudEstanciaVo.setId((Integer) objects[0]);
        solicitudEstanciaVo.setIdGerencia((Integer) objects[1]);
        solicitudEstanciaVo.setIdUsuario((String) objects[2]);
        solicitudEstanciaVo.setIdSgMotivo((Integer) objects[3]);
        solicitudEstanciaVo.setCodigo((String) objects[4]);
        solicitudEstanciaVo.setNombreGerencia((String) objects[5]);
        solicitudEstanciaVo.setInicioEstancia((Date) objects[6]);
        solicitudEstanciaVo.setFinEstancia((Date) objects[7]);
        solicitudEstanciaVo.setNombre((String) objects[8]);
        solicitudEstanciaVo.setNombreSgMotivo((String) objects[9]);
        solicitudEstanciaVo.setIdSgOfina((Integer) objects[10]);
        solicitudEstanciaVo.setNombreSgOficina((String) objects[11]);
        solicitudEstanciaVo.setCorreoGenero((String) objects[12]);

        return solicitudEstanciaVo;
    }

    
    public SgSolicitudEstanciaVo buscarEstanciaPorId(int idSolEstancia) {

        StringBuilder sb = new StringBuilder();
        sb.append("select se.ID, "
                + "se.CODIGO, "
                + "se.INICIO_ESTANCIA, "
                + "se.FIN_ESTANCIA, "
                + "se.DIAS_ESTANCIA, "
                + "se.SG_OFICINA as idSgOficina, "
                + "o.NOMBRE as nombreSgOficina, "
                + "se.ESTATUS as idEstatus, "
                + "e.NOMBRE as nombreEstatus, "
                + "g.ID as idGerencia, "
                + "g.NOMBRE as nombreGerencia, "
                + "se.SG_MOTIVO as idSgMotivo, "
                + "m.NOMBRE as nombreSgMotivo, "
                + "se.FECHA_GENERO, "
                + "se.GENERO, "
                + "u.email, "
                + "u.nombre "
                + "from SG_SOLICITUD_ESTANCIA se "
                + "inner join SG_OFICINA o on o.ID = se.SG_OFICINA "
                + "inner join ESTATUS e on e.ID = se.ESTATUS "
                + "inner join GERENCIA g on g.ID = se.GERENCIA "
                + "inner join SG_MOTIVO m on m.id = se.SG_MOTIVO "
                + "inner join USUARIO u on u.ID = se.GENERO "
                + "where se.id = ? "
                + "and  se.ELIMINADO= ? "
                + "and o.ELIMINADO= ? "
                + "and m.ELIMINADO= ? "
                + "and e.ELIMINADO= ? "
                + "and g.ELIMINADO= ? ");
        // appendQuery(" and se.usuario_hospeda  = uh.id");

        Object[] objects = (Object[]) em.createNativeQuery(sb.toString())
                .setParameter(1, idSolEstancia)
                .setParameter(2, Constantes.NO_ELIMINADO)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .setParameter(6, Constantes.NO_ELIMINADO)
                .getSingleResult();
        SgSolicitudEstanciaVo vo;
        if (objects != null) {
            vo = new SgSolicitudEstanciaVo();
            vo.setId((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setInicioEstancia((Date) objects[2]);
            vo.setFinEstancia((Date) objects[3]);
            vo.setDiasEstancia((Integer) objects[4]);
            vo.setIdSgOfina((Integer) objects[5]);
            vo.setNombreSgOficina((String) objects[6]);
            vo.setIdEstatus((Integer) objects[7]);
            vo.setNombreEstatus((String) objects[8]);
            vo.setIdGerencia((Integer) objects[9]);
            vo.setNombreGerencia((String) objects[10]);
            vo.setIdSgMotivo((Integer) objects[11]);
            vo.setNombreSgMotivo((String) objects[12]);
            vo.setFechaGenero((Date) objects[13]);
            vo.setGenero((String) objects[14]);
            vo.setIdUsuario((String) objects[14]);
            vo.setCorreoGenero((String) objects[15]);
            vo.setNombreGenero((String) objects[16]);
            //vo.setCorreoUsuarioHospeda((String) objects[17]);
            //vo.setIdUsuarioHospeda((String) objects[18]);
            UtilLog4j.log.debug(this, "Se encontraró " + vo.toString());
            vo.setDetalle(sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(vo.getId(), Constantes.NO_ELIMINADO));
            return vo;
        } else {
            return null;
        }
    }

    
    public void finalizaSolicitud(int idSolEstancia, String idUsuario) {
        SgSolicitudEstancia solicitudEstancia = find(idSolEstancia);
        solicitudEstancia.setModifico(new Usuario(idUsuario));
        solicitudEstancia.setFechaModifico(new Date());
        solicitudEstancia.setHoraModifico(new Date());
        solicitudEstancia.setEstatus(estatusRemote.find(40));
        edit(solicitudEstancia);
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
        //UtilLog4j.log.info(this,mensaje);
    }

    /**
     * Cancela la solicitud sin enviar notificaciones
     *
     * @param idUsuarioRealizo
     * @param idSgSolicitudEstancia
     * @param motivo
     */
    
    public void cancelarSolicitudEstanciaAntesSolicitar(String idUsuarioRealizo, int idSgSolicitudEstancia, String motivo) {
        //Se registra el cambio
        SgSolicitudEstancia solicitudEstancia = find(idSgSolicitudEstancia);

        UtilLog4j.log.info(this, "Estancia id: " + solicitudEstancia.getId());
        solicitudEstancia.setModifico(new Usuario(idUsuarioRealizo));
        solicitudEstancia.setFechaModifico(new Date());
        solicitudEstancia.setHoraModifico(new Date());
        solicitudEstancia.setCancelado(Constantes.BOOLEAN_TRUE);
        solicitudEstancia.setEstatus(estatusRemote.find(50));
        edit(solicitudEstancia);
        //enviar a si movimiento
        SiMovimiento simo = this.siMovimientoService.save(motivo, 3, idUsuarioRealizo);
        if (simo != null) {
            sgSolicitudEstanciaSiMovimientoRemote.guardarSiMovimiento(solicitudEstancia.getId(), simo.getId(), idUsuarioRealizo);
        }
    }

    
    public List<SgSolicitudEstanciaVo> solicituesPorAprobar(String id) {
        String q = "select se.ID, se.INICIO_ESTANCIA, se.FIN_ESTANCIA, se.DIAS_ESTANCIA,"
                + " se.CODIGO, o.NOMBRE, m.NOMBRE, g.NOMBRE, u.nombre as genero  "
                + " from SG_SOLICITUD_ESTANCIA se"
                + "	inner join SG_OFICINA o on se.SG_OFICINA = o.ID"
                + "	inner join SG_MOTIVO m on se.SG_MOTIVO = m.ID"
                + "	inner join GERENCIA g on se.GERENCIA = g.ID"
                + "	inner join usuario u on u.id = se.genero"
                + "	inner join AP_CAMPO_GERENCIA cg on cg.GERENCIA = g.ID  and cg.AP_CAMPO = ? "
                + " where cg.RESPONSABLE = ? "
                + " and se.ESTATUS = ?"
                + " and se.ELIMINADO = 'False'";
        Query qr = em.createNativeQuery(q);
        qr.setParameter(1, Constantes.AP_CAMPO_DEFAULT);
        qr.setParameter(2, id);
        qr.setParameter(3, Constantes.REQUISICION_VISTO_BUENO);
        List<Object[]> lo = qr.getResultList();
        List<SgSolicitudEstanciaVo> les = null;
        if (lo != null) {
            les = new ArrayList<SgSolicitudEstanciaVo>();
            for (Object[] lo1 : lo) {
                SgSolicitudEstanciaVo vo = new SgSolicitudEstanciaVo();
                vo.setId((Integer) lo1[0]);
                vo.setInicioEstancia((Date) lo1[1]);
                vo.setFinEstancia((Date) lo1[2]);
                vo.setDiasEstancia((Integer) lo1[3]);
                vo.setCodigo((String) lo1[4]);
                vo.setNombreSgOficina((String) lo1[5]);
                vo.setNombreSgMotivo((String) lo1[6]);
                vo.setNombreGerencia((String) lo1[7]);
                vo.setNombreGenero((String) lo1[8]);
                vo.setDetalle(sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(vo.getId(), Constantes.NO_ELIMINADO));
                les.add(vo);
            }

        }
        return les;
    }

    
    public long totalSolicituesPorAprobar(String sesion) {
        String q = "select count(se.ID) from SG_SOLICITUD_ESTANCIA se"
                + "	inner join GERENCIA g on se.GERENCIA = g.ID"
                + "	inner join AP_CAMPO_GERENCIA cg on cg.GERENCIA = g.ID  and cg.AP_CAMPO = ? "
                + " where cg.RESPONSABLE = ? "
                + " and se.ESTATUS = ?"
                + " and se.ELIMINADO = 'False'";
        Query qr = em.createNativeQuery(q);
        qr.setParameter(1, Constantes.AP_CAMPO_DEFAULT);
        qr.setParameter(2, sesion);
        qr.setParameter(3, Constantes.REQUISICION_VISTO_BUENO);
        return (Long) qr.getSingleResult();
    }

    
    public boolean aprobarEstancia(String sesion, int solicitudEstanciaVo) {
        boolean regresa = false;
        SgSolicitudEstancia sgSolicitudEstancia = find(solicitudEstanciaVo);
        sgSolicitudEstancia.setEstatus(estatusRemote.find(Constantes.REQUISICION_SOLICITADA));
        sgSolicitudEstancia.setModifico(usuarioRemote.find(sesion));
        sgSolicitudEstancia.setFechaModifico(new Date());
        sgSolicitudEstancia.setHoraModifico(new Date());

        if (notificacionServiciosGeneralesRemote.enviarCorreoSolicitaEstancia(Constantes.AP_CAMPO_DEFAULT, buscarEstanciaPorId(solicitudEstanciaVo), sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(sgSolicitudEstancia.getId(), Constantes.NO_ELIMINADO))) {
            edit(sgSolicitudEstancia);
            regresa = true;
        }
        return regresa;
    }

    private void enviarNotificacion(String titulo, String mensaje, String usuarioDestino) {
        try {
            //
            List<SiUsuarioCodigo> lu = siUsuarioCodigoLocal.buscarPorUsuario(usuarioDestino);
            if (lu != null && !lu.isEmpty()) {
                for (SiUsuarioCodigo lu1 : lu) {
                    FCMSender.notificaciones(titulo, mensaje, lu1.getToken(), Constantes.ESTANCIA_TOKEN);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public SgSolicitudEstancia guardarEnviarSolicitud(String idUsuario, int idOficina, int idMotivo, int idGerencia,
            Date fechaInicio, Date fechaFin, List<DetalleEstanciaVO> detalle, String observacion) {
        UtilLog4j.log.info(this, "SgSolicitudEstanciaImpl.guardarSolicitud()");
        int status;
        boolean v;
        SgSolicitudEstancia sgSolicitudEstancia = new SgSolicitudEstancia();
        sgSolicitudEstancia.setCodigo("SE" + getDigitosAño(new Date()) + "-" + Integer.toString(this.folioRemote.getFolio("SOLICITUD_ESTANCIA_CONSECUTIVO")));
        sgSolicitudEstancia.setDiasEstancia(siManejoFechaLocal.dias(fechaFin, fechaInicio));
        sgSolicitudEstancia.setSgOficina(sgOficinaRemote.find(idOficina));
        sgSolicitudEstancia.setGerencia(gerenciaRemote.find(idGerencia));
        sgSolicitudEstancia.setSgMotivo(sgMotivoRemote.find(idMotivo));
        sgSolicitudEstancia.setGenero(usuarioRemote.find(idUsuario));
        sgSolicitudEstancia.setInicioEstancia(fechaInicio);
        sgSolicitudEstancia.setFinEstancia(fechaFin);
        sgSolicitudEstancia.setObservacion(observacion);
        //
        sgSolicitudEstancia.setFechaGenero(new Date());
        sgSolicitudEstancia.setHoraGenero(new Date());
        sgSolicitudEstancia.setEliminado(Constantes.BOOLEAN_FALSE);
        sgSolicitudEstancia.setCancelado(Constantes.BOOLEAN_FALSE);
        //
        SgSolicitudEstanciaVo solicitudEstanciaVo = new SgSolicitudEstanciaVo();
        solicitudEstanciaVo.setCodigo(sgSolicitudEstancia.getCodigo());
        solicitudEstanciaVo.setDiasEstancia(sgSolicitudEstancia.getDiasEstancia());
        solicitudEstanciaVo.setNombreSgOficina(sgSolicitudEstancia.getSgOficina().getNombre());
        solicitudEstanciaVo.setIdSgOfina(sgSolicitudEstancia.getSgOficina().getId());
        solicitudEstanciaVo.setNombreGerencia(sgSolicitudEstancia.getGerencia().getNombre());
        solicitudEstanciaVo.setIdGerencia(sgSolicitudEstancia.getGerencia().getId());
        solicitudEstanciaVo.setInicioEstancia(sgSolicitudEstancia.getInicioEstancia());
        solicitudEstanciaVo.setObservacion(sgSolicitudEstancia.getObservacion());
        solicitudEstanciaVo.setFinEstancia(sgSolicitudEstancia.getFinEstancia());
        solicitudEstanciaVo.setCorreoGenero(sgSolicitudEstancia.getGenero().getEmail());
        solicitudEstanciaVo.setNombreGenero(sgSolicitudEstancia.getGenero().getNombre());
        solicitudEstanciaVo.setNombreSgMotivo(sgSolicitudEstancia.getSgMotivo().getNombre());
        //
        UsuarioResponsableGerenciaVo urgv = apCampoGerenciaRemote.buscarResponsablePorGerencia(sgSolicitudEstancia.getGerencia().getId(), Constantes.AP_CAMPO_DEFAULT);

        
        if (usuarioRemote.isGerente(Constantes.AP_CAMPO_DEFAULT, idUsuario)) {
            v = notificacionServiciosGeneralesRemote.enviarCorreoSolicitaEstancia(Constantes.AP_CAMPO_DEFAULT, solicitudEstanciaVo, detalle);
            status = Constantes.REQUISICION_SOLICITADA;
            sgSolicitudEstancia.setEstatus(estatusRemote.find(status));
            create(sgSolicitudEstancia);
        } else {
            status = Constantes.REQUISICION_VISTO_BUENO;
            sgSolicitudEstancia.setEstatus(estatusRemote.find(status));
            create(sgSolicitudEstancia);
            //
            solicitudEstanciaVo.setId(sgSolicitudEstancia.getId());
            v = notificacionServiciosGeneralesRemote.enviarCorreoAprobarEstancia(solicitudEstanciaVo, detalle);
            //
            enviarNotificacion(Constantes.TITULO_SOL_ESTANCIA_NOTIFICACION, sgSolicitudEstancia.getSgMotivo().getNombre(), urgv.getIdUsuario());
        }
        //
        for (DetalleEstanciaVO detalleEstanciaVO : detalle) {
            sgDetalleSolicitudEstanciaRemote.guardarHuespededSolicitudEstancia(idUsuario, sgSolicitudEstancia.getId(), detalleEstanciaVO.getIdInvitado() < 1, detalleEstanciaVO.getIdUsuario(), detalleEstanciaVO.getIdInvitado(),null );
        }
        
        return sgSolicitudEstancia;
    }

}
