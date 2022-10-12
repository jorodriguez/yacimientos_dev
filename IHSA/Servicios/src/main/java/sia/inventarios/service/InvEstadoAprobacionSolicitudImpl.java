/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.service.notificacion.NotificacionInventarioImpl;
import sia.modelo.Estatus;
import sia.modelo.InvEstadoAprobacionSolicitud;
import sia.modelo.InvSolicitudMaterial;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.EstadoAprobacionSolicitudVo;
import sia.modelo.vo.inventarios.InvSolicitudMovimientoImpl;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;
import sia.modelo.vo.inventarios.TransaccionVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.SolicitudMaterialEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class InvEstadoAprobacionSolicitudImpl extends AbstractFacade<InvEstadoAprobacionSolicitud>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvEstadoAprobacionSolicitudImpl() {
        super(InvEstadoAprobacionSolicitud.class);
    }
    @Inject
    InvDetalleSolicitudMaterialImpl  detalleSolicitudMaterialLocal;
    @Inject
    InvSolicitudMaterialImpl solicitudMaterialLocal;
    @Inject
    NotificacionInventarioImpl  notificacionInventarioLocal;
    @Inject
    SiRolImpl rolRemote;
    @Inject
    InvSolicitudMovimientoImpl solicitudMovimientoLocal;
    @Inject
    InventarioImpl inventarioRemote;
    @Inject
    UsuarioImpl usuarioRemote;
    @Inject
    SiUsuarioRolImpl usuarioRolRemote;
    @Inject
    InventarioMovimientoImpl inventarioMovimientoService;
    @Inject
    TransaccionRemote transaccionRemote;
    @Inject
    TransaccionArticuloRemote transaccionArticuloRemote;

    
    public void guardar(int idSolicitud, String sesion, String idUsuario, int status) {
        InvEstadoAprobacionSolicitud estadoAprobacionSolicitud = new InvEstadoAprobacionSolicitud();
        estadoAprobacionSolicitud.setInvSolicitudMaterial(new InvSolicitudMaterial(idSolicitud));
        estadoAprobacionSolicitud.setUsuario(new Usuario(idUsuario));
        estadoAprobacionSolicitud.setEstatus(new Estatus(status));
        estadoAprobacionSolicitud.setActual(Constantes.BOOLEAN_TRUE);
        //
        estadoAprobacionSolicitud.setGenero(new Usuario(sesion));
        estadoAprobacionSolicitud.setFechaGenero(new Date());
        estadoAprobacionSolicitud.setHoraGenero(new Date());
        estadoAprobacionSolicitud.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(estadoAprobacionSolicitud);
    }

    
    public void autrizarSolicitud(int idSolicitud, String sesion, int idCampo) {
        //
        SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo = solicitudMaterialLocal.solicitudesPorId(idSolicitud);
        notificacionInventarioLocal.enviarCorreoAutorizarSolicitud(solicitudMaterialAlmacenVo, idCampo);
        //
        cambioEstado(solicitudMaterialAlmacenVo, sesion, SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId(), SolicitudMaterialEstadoEnum.POR_ENTREGAR_MATERIAL.getId());
        //
    }

    
    public void rechazarSolicitud(int idSolicitud, String sesion, String motivo, int idCampo) {
        //
        SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo = solicitudMaterialLocal.solicitudesPorId(idSolicitud);
        notificacionInventarioLocal.enviarCorreoRechazoSolicitud(solicitudMaterialAlmacenVo, motivo, idCampo);
        // guarda el movimiento
        solicitudMovimientoLocal.guardar(sesion, idSolicitud, motivo, Constantes.ID_SI_OPERACION_DEVOLVER);
        //
        //cambia es status de la solicitud
        cambioEstado(solicitudMaterialAlmacenVo, sesion, SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId(), SolicitudMaterialEstadoEnum.SOLICITUD_CREADA.getId());

    }

    
    public InvEstadoAprobacionSolicitud traerSolicitudporStatus(int idSolicitud, int status) {
        try {
            return (InvEstadoAprobacionSolicitud) em.createNamedQuery("InvEstadoAprobacionSolicitud.findFacturaStatus").setParameter(1, idSolicitud).setParameter(2, status).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }

    
    public List<SolicitudMaterialAlmacenVo> traerSolicitudesUsuarioStatus(int idCampo, String usuarioSesion, int status) {
        String c = consulta()
                + " where 1 = 1 "
                + " and eas.usuario  = '" + usuarioSesion + "'"
                + " and ism.estatus = " + status
                + " and ism.ap_campo  = " + idCampo
                + " and eas.actual = " + Constantes.BOOLEAN_TRUE
                + " and ism.eliminado = false ";
        List<Object[]> objects = em.createNativeQuery(c).getResultList();
        List<SolicitudMaterialAlmacenVo> lista = new ArrayList<>();
        for (Object[] object : objects) {
            lista.add(cast(object));
        }
        return lista;
    }

    private String consulta() {
        String c = "select ism.id, g.id as idGerencia, g.nombre as gerencia, a.id as idAlmacen, a.nombre as almacen, ism.folio, ism.fecha_requerida, "
                + " (select sum(idsm.cantidad) from inv_detalle_solicitud_material idsm where idsm.inv_solicitud_material  = ism.id  and idsm.eliminado = false), "
                + " (select count(dsm.id ) from inv_detalle_solicitud_material dsm where dsm.inv_solicitud_material  = ism.id  and dsm.eliminado = false),  "
                + " coalesce(ism.observaciones, ''),  coalesce(ism.telefono , ''), ism.usuario_recoge , us.nombre, us.email, ism.fecha_solicita, "
                + " userA.id, userA.nombre, es.id, es.nombre, ism.usuario_recibe_material, eas.id "
                + "  from inv_solicitud_material ism\n"
                + "	inner join inv_almacen a on ism.inv_almacen  = a.id \n"
                + "	inner join gerencia  g on ism.gerencia = g.id \n"
                + "     inner join usuario us on ism.genero = us.id "
                + "     inner join inv_estado_aprobacion_solicitud eas on eas.inv_solicitud_material = ism.id "
                + "     left  join usuario userA on eas.usuario = userA.id "
                + "     inner join estatus es on eas.estatus = es.id ";
        return c;

    }

    private SolicitudMaterialAlmacenVo cast(Object[] objs) {
        SolicitudMaterialAlmacenVo smav = new SolicitudMaterialAlmacenVo();
        smav.setId((Integer) objs[0]);
        smav.setIdGerencia((Integer) objs[1]);
        smav.setGerencia((String) objs[2]);
        smav.setIdAlmacen((Integer) objs[3]);
        smav.setAlmacen((String) objs[4]);
        smav.setFolio((String) objs[5]);
        smav.setFechaRequiere((Date) objs[6]);
        smav.setCantidadSolicitada(((BigDecimal) objs[7]).doubleValue());
        smav.setTotalDetalle(((Long) objs[8]));
        smav.setObservacion(((String) objs[9]));
        smav.setTelefono(((String) objs[10]));
        smav.setUsuarioRecoge(((String) objs[11]));
        smav.setSolicita(((String) objs[12]));
        smav.setCorreoSolicita(((String) objs[13]));
        smav.setFechaSolicita(((Date) objs[14]));
        smav.setIdAutoriza(((String) objs[15]));
        smav.setAutoriza(((String) objs[16]));
        smav.setIdStatus(((Integer) objs[17]));
        smav.setStatus(((String) objs[18]));
        smav.setUsuarioRecibeMaterial(((String) objs[19]));
        smav.setIdEstadoAprobacion(((Integer) objs[20]));
        //
        smav.setMateriales(new ArrayList<>());
        smav.setMateriales(detalleSolicitudMaterialLocal.traerPorSolicitudId(smav.getId()));
        smav.setSelected(Constantes.BOOLEAN_FALSE);
        return smav;
    }

    
    public List<SolicitudMaterialAlmacenVo> traerSolicitudesRolStatus(int idCampo, String usuarioSesion, int status, String codigoRol) {
        String c = consulta()
                + " where 1 = 1 "
                + " and '" + usuarioSesion + "'" + " in (select ur.usuario from si_usuario_rol ur "
                + "                                         inner join si_rol rol on ur.si_rol = rol.id "
                + "                                   where rol.codigo = '" + codigoRol + "'"
                + "                                   and ur.eliminado = false and ur.ap_campo = " + idCampo + " )"
                + " and eas.estatus = " + status
                + " and ism.ap_campo  = " + idCampo
                + " and eas.actual = " + Constantes.BOOLEAN_TRUE
                + " and ism.eliminado = false ";
        List<Object[]> objects = em.createNativeQuery(c).getResultList();
        List<SolicitudMaterialAlmacenVo> lista = new ArrayList<>();
        for (Object[] object : objects) {
            lista.add(cast(object));
        }
        return lista;
    }

    /**
     *
     * @param status
     * @return
     */
    
    public List<SolicitudMaterialAlmacenVo> traerSolicitudesPorStatus(int status) {
        String c = consulta()
                + " where 1 = 1 "
                + " and eas.estatus = " + status
                + " and eas.actual = " + Constantes.BOOLEAN_TRUE
                + " and ism.eliminado = false ";
        List<Object[]> objects = em.createNativeQuery(c).getResultList();
        List<SolicitudMaterialAlmacenVo> lista = new ArrayList<>();
        for (Object[] object : objects) {
            lista.add(cast(object));
        }
        return lista;
    }

    
    public void entregarMaterial(SolicitudMaterialAlmacenVo smVo, UsuarioVO sesion) {
        //actualiza cantidad material entregado
        detalleSolicitudMaterialLocal.actualizaCantidadRecibida(smVo.getMateriales(), sesion.getId());

        //cambia es status de la solicitud
        cambioEstado(smVo, sesion.getId(), SolicitudMaterialEstadoEnum.POR_ENTREGAR_MATERIAL.getId(), SolicitudMaterialEstadoEnum.SOLICITUD_TERMINADA.getId());

        solicitudMaterialLocal.actualizarUsuarioRecibeMaterial(smVo, sesion.getId());
        //actualizar el inventario
        try {

            Integer tipoMovimiento = Constantes.INV_MOVIMIENTO_TIPO_SALIDA;
            TransaccionVO transaccionVO = new TransaccionVO();
            transaccionVO.setAlmacenId(smVo.getIdAlmacen());
            transaccionVO.setTipoMovimiento(tipoMovimiento);
            transaccionVO.setFecha(new Date());
            transaccionVO.setNumeroArticulos((int) smVo.getCantidadRecibida());
            transaccionVO.setFolioRemision(smVo.getFolio());
            transaccionVO.setIdSolicitud(smVo.getId());

            transaccionRemote.crearConciliar(transaccionVO, null, sesion.getId(), sesion.getIdCampo());
            //
            // audit.register(AuditActions.CREATE, transaccion, user);
            for (DetalleSolicitudMaterialAlmacenVo materiale : smVo.getMateriales()) {
                // inventarioRemote.salidaInventario(materiale.getIdInventario(), materiale.getIdArticulo(), smVo.getIdAlmacen(), ((Double) materiale.getCantidadRecibida()).intValue(), sesion.getId(), smVo.getIdCampo(), smVo.getFolio());
                //
                TransaccionArticuloVO transaccionArticuloVO = new TransaccionArticuloVO();
                transaccionArticuloVO.setArticuloId(materiale.getIdArticulo());
                transaccionArticuloVO.setNumeroUnidades(materiale.getCantidadRecibida());
                transaccionArticuloVO.setTransaccionId(transaccionVO.getId());                
                transaccionArticuloRemote.crear(transaccionArticuloVO, sesion.getId(), smVo.getIdCampo());
                //
            }
            transaccionRemote.procesar(transaccionVO.getId(), sesion.getId(), sesion.getIdCampo());
            //
            //Notificacion de entrega
            notificacionInventarioLocal.enviarCorreoEntregaMaterial(smVo, sesion);

        } catch (SIAException ex) {
            UtilLog4j.log.error(ex);
        }

    }

    
    public void vistoBuenoEntrega(SolicitudMaterialAlmacenVo smVo, String sesion) {
        cambioEstado(smVo, sesion, SolicitudMaterialEstadoEnum.MATERIAL_ENTREGADO.getId(), SolicitudMaterialEstadoEnum.SOLICITUD_TERMINADA.getId());
        //

    }

    private void cambioEstado(SolicitudMaterialAlmacenVo smVo, String sesion, int statusActual, int statusNuevo) {
        //cambia es status de la solicitud
        solicitudMaterialLocal.actualizarEstatus(smVo.getId(), statusNuevo, sesion);
        // cambia el estado del campo actual.
        InvEstadoAprobacionSolicitud estadoAprobacionSolicitud = traerSolicitudporStatus(smVo.getId(), statusActual);
        estadoAprobacionSolicitud.setActual(Constantes.BOOLEAN_FALSE);
        estadoAprobacionSolicitud.setModifico(new Usuario(sesion));
        estadoAprobacionSolicitud.setFechaModifico(new Date());
        estadoAprobacionSolicitud.setHoraModifico(new Date());
        edit(estadoAprobacionSolicitud);
        //
        //crea el nuevo registro
        InvEstadoAprobacionSolicitud invEstadoAprobacionSolicitud = new InvEstadoAprobacionSolicitud();
        invEstadoAprobacionSolicitud.setInvSolicitudMaterial(new InvSolicitudMaterial(smVo.getId()));
        invEstadoAprobacionSolicitud.setUsuario(new Usuario(sesion));
        invEstadoAprobacionSolicitud.setEstatus(new Estatus(statusNuevo));
        invEstadoAprobacionSolicitud.setActual(Constantes.BOOLEAN_TRUE);
        invEstadoAprobacionSolicitud.setGenero(new Usuario(sesion));
        invEstadoAprobacionSolicitud.setFechaGenero(new Date());
        invEstadoAprobacionSolicitud.setHoraGenero(new Date());
        invEstadoAprobacionSolicitud.setEliminado(Constantes.NO_ELIMINADO);
        create(invEstadoAprobacionSolicitud);
    }

    
    public List<SolicitudMaterialAlmacenVo> traerPorSolicitud(int idSolicitud) {
        String c = consulta()
                + " where 1 = 1 "
                + " and ism.inv_solicitud_material  = " + idSolicitud
                + " and ism.eliminado = false ";
        List<Object[]> objects = em.createNativeQuery(c).getResultList();
        List<SolicitudMaterialAlmacenVo> lista = new ArrayList<>();
        for (Object[] object : objects) {
            lista.add(cast(object));
        }
        return lista;
    }

    
    public List<EstadoAprobacionSolicitudVo> traerProcesoAprobacionPorSolicitud(int idSol) {
        String c = " select ieas.id, u.id, u.nombre, e.id, e.nombre, ieas.fecha_modifico, ieas.hora_modifico, ieas.actual, ieas.fecha_genero , ieas.hora_genero \n"
                + " from inv_estado_aprobacion_solicitud ieas \n"
                + " 	inner join usuario u on ieas.usuario = u.id \n"
                + " 	inner join estatus e on ieas.estatus = e.id \n"
                + " where ieas.inv_solicitud_material = " + idSol
                + " and ieas.eliminado = false "
                + " order by ieas.id";
        List<Object[]> objs = em.createNativeQuery(c).getResultList();
        List<EstadoAprobacionSolicitudVo> eas = new ArrayList<>();
        for (Object[] obj : objs) {
            EstadoAprobacionSolicitudVo eVo = new EstadoAprobacionSolicitudVo();
            eVo.setId((Integer) obj[0]);
            eVo.setIdUsuario((String) obj[1]);
            eVo.setUsuario((String) obj[2]);
            eVo.setIdEstatus((Integer) obj[3]);
            eVo.setStatus((String) obj[4]);
            eVo.setFecaModifico((Date) obj[5]);
            eVo.setHoraModifico((Date) obj[6]);
            eVo.setActual((Boolean) obj[7]);
            eVo.setFechaGenero((Date) obj[8]);
            eVo.setHoraGenero((Date) obj[9]);
            eas.add(eVo);
        }
        return eas;
    }

    
    public long totalSolicitudesUsuarioStatus(String usuarioSesion, int status) {
        String c = "select count(ieas.id) \n"
                + " from inv_estado_aprobacion_solicitud ieas \n"
                + " where  ieas.usuario  = '" + usuarioSesion + "'"
                + " and ieas.estatus = " + status
                + " and ieas.actual = " + Constantes.BOOLEAN_TRUE
                + " and ieas.eliminado = false ";
        //
        return (long) em.createNativeQuery(c).getSingleResult();
    }

    
    public boolean pasarSolicitud(List<SolicitudMaterialAlmacenVo> solicitudes, String usuarioTiene, String usuarioAprobara, UsuarioVO sesion, int idStatus, int idcampo) {
        Usuario usTiene = usuarioRemote.buscarPorId(usuarioTiene);
        Usuario usApr = usuarioRemote.buscarPorId(usuarioAprobara);
        // verifica rol
        if (!usuarioRolRemote.buscarRolPorUsuarioModulo(usuarioAprobara, Constantes.MODULO_INVENTARIOS, Constantes.ROL_AUTORIZA_MAT, idcampo)) {
            usuarioRolRemote.guardarUsuarioRol(Constantes.ROL_AUTORIZA_MAT, usuarioAprobara, idcampo, sesion.getId());
        }
        for (SolicitudMaterialAlmacenVo solicitude : solicitudes) {
            InvEstadoAprobacionSolicitud eas = find(solicitude.getIdEstadoAprobacion());
            eas.setUsuario(usApr);
            eas.setModifico(new Usuario(sesion.getId()));
            eas.setFechaModifico(new Date());
            eas.setHoraModifico(new Date());
            //
            edit(eas);
        }
        return notificacionInventarioLocal.enviarCorreoCambioSolicitudMaterial(solicitudes, usTiene, usApr, sesion.getMail());
    }

    
    public void cancelarSolicitud(int idSolicitud, String sesion, String motivo, int idCampo) {
        //
        SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo = solicitudMaterialLocal.solicitudesPorId(idSolicitud);
        notificacionInventarioLocal.enviarCorreoCancelaSolicitud(solicitudMaterialAlmacenVo, motivo, idCampo);
        // guarda el movimiento
        solicitudMovimientoLocal.guardar(sesion, idSolicitud, motivo, Constantes.ID_SI_OPERACION_CANCELAR);
        //
        //cambia es status de la solicitud
        cambioEstado(solicitudMaterialAlmacenVo, sesion, SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId(), SolicitudMaterialEstadoEnum.SOLICITUD_CANCELADA.getId());

    }

    
    public void finalizarSolicitudMaterial(SolicitudMaterialAlmacenVo smVo, UsuarioVO sesion, String motivo) {
        //cambia es status de la solicitud
        cambioEstado(smVo, sesion.getId(), SolicitudMaterialEstadoEnum.POR_ENTREGAR_MATERIAL.getId(), SolicitudMaterialEstadoEnum.SOLICITUD_TERMINADA.getId());
        // guarda el movimiento
        solicitudMovimientoLocal.guardar(sesion.getId(), smVo.getId(), motivo, Constantes.ID_SI_OPERACION_CANCELAR);

    }
}
