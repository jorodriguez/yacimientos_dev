/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.inventarios.service.notificacion.NotificacionInventarioImpl;
import sia.modelo.ApCampo;
import sia.modelo.Estatus;
import sia.modelo.Gerencia;
import sia.modelo.InvAlmacen;
import sia.modelo.InvEstadoAprobacionSolicitud;
import sia.modelo.InvSolicitudMaterial;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.servicios.sistema.impl.FolioImpl;
import sia.util.SolicitudMaterialEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class InvSolicitudMaterialImpl extends AbstractFacade<InvSolicitudMaterial>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvSolicitudMaterialImpl() {
        super(InvSolicitudMaterial.class);
    }

    @Inject
    InvDetalleSolicitudMaterialImpl  detalleSolicitudMaterialLocal;
    @Inject
    FolioImpl folioRemote;
    @Inject
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudLocal;
    @Inject
    NotificacionInventarioImpl notificacionInventarioLocal;

    
    public List<SolicitudMaterialAlmacenVo> traerSolicitudesGenero(int idCampo, String usuarioSesion) {
        String c = consulta()
                + " where ism.genero  = '" + usuarioSesion + "'"
                + " and ism.estatus = " + SolicitudMaterialEstadoEnum.SOLICITUD_CREADA.getId()
                + " and ism.ap_campo  = " + idCampo
                + " and ism.eliminado = false ";
        List<Object[]> objects = em.createNativeQuery(c).getResultList();
        List<SolicitudMaterialAlmacenVo> lista = new ArrayList<>();
        for (Object[] object : objects) {
            lista.add(cast(object));
        }
        return lista;
    }

    
    public SolicitudMaterialAlmacenVo solicitudesPorId(int idSolicitud) {
        String c = consulta()
                + " where ism.id  = " + idSolicitud
                + " and ism.eliminado = false ";
        Object[] objects = (Object[]) em.createNativeQuery(c).getSingleResult();
        return cast(objects);
    }

    private String consulta() {
        String c = "select ism.id, g.id as idGerencia, g.nombre as gerencia, a.id as idAlmacen, a.nombre as almacen, ism.folio, ism.fecha_requerida, "
                + " coalesce((select sum(idsm.cantidad) from inv_detalle_solicitud_material idsm where idsm.inv_solicitud_material  = ism.id  and idsm.eliminado = false), 0), "
                + " (select count(dsm.id ) from inv_detalle_solicitud_material dsm where dsm.inv_solicitud_material  = ism.id  and dsm.eliminado = false),  "
                + " coalesce(ism.observaciones, ''),  coalesce(ism.telefono , ''), ism.usuario_recoge , us.nombre, us.email, us.genero,"
                + " (select count(sol_mov.id) from inv_solicitud_movimiento sol_mov  where sol_mov.inv_solicitud_material  = ism.id and sol_mov.eliminado  = false ),"
                + " ism.usuario_recibe_material , e.id, e.nombre, ism.fecha_entrega, ism.hora_entrega"
                + " from inv_solicitud_material ism\n"
                + "	inner join inv_almacen a on ism.inv_almacen  = a.id \n"
                + "	inner join gerencia  g on ism.gerencia = g.id \n"
                + "     inner join usuario us on ism.genero = us.id"
                + "     inner join estatus e on ism.estatus  = e.id";
        return c;
    }

    
    public void guardar(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, String sesion, int campo) {
        crearSolicitud(solicitudMaterialAlmacenVo, sesion, campo);
    }

    
    public void guardarSolicitar(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, String sesion, int campo, String idAutoriza) {
        int idSol = crearSolicitud(solicitudMaterialAlmacenVo, sesion, campo);
        solicitudMaterialAlmacenVo = solicitudesPorId(idSol);
        //solicitar
        notificacionInventarioLocal.enviarCorreoAutorizarMaterial(solicitudMaterialAlmacenVo, idAutoriza);
        //actualizar
        InvEstadoAprobacionSolicitud estadoAprobacionSolicitud = estadoAprobacionSolicitudLocal.traerSolicitudporStatus(idSol, SolicitudMaterialEstadoEnum.SOLICITUD_CREADA.getId());
        estadoAprobacionSolicitud.setActual(Constantes.BOOLEAN_FALSE);
        estadoAprobacionSolicitud.setModifico(new Usuario(sesion));
        estadoAprobacionSolicitud.setFechaModifico(new Date());
        estadoAprobacionSolicitud.setHoraModifico(new Date());
        //
        estadoAprobacionSolicitudLocal.edit(estadoAprobacionSolicitud);

        estadoAprobacionSolicitudLocal.guardar(idSol, sesion, idAutoriza, SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId());
        //
        InvSolicitudMaterial sm = find(solicitudMaterialAlmacenVo.getId());
        sm.setFechaSolicita(new Date());
        sm.setEstatus(new Estatus(SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId()));
        sm.setModifico(new Usuario(sesion));
        sm.setFechaModifico(new Date());
        sm.setHoraModifico(new Date());
        //
        edit(sm);

    }

    private int crearSolicitud(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, String sesion, int campo) {
        try {
            InvSolicitudMaterial solicitudMaterial;
            if (solicitudMaterialAlmacenVo.getId() == 0) {
                solicitudMaterial = new InvSolicitudMaterial();
            } else {
                solicitudMaterial = find(solicitudMaterialAlmacenVo.getId());
            }
            solicitudMaterial.setApCampo(new ApCampo(campo));
            solicitudMaterial.setInvAlmacen(new InvAlmacen(solicitudMaterialAlmacenVo.getIdAlmacen()));
            solicitudMaterial.setEstatus(new Estatus(SolicitudMaterialEstadoEnum.SOLICITUD_CREADA.getId()));
            solicitudMaterial.setGerencia(new Gerencia(solicitudMaterialAlmacenVo.getIdGerencia()));
            solicitudMaterial.setCantidadSolicitada(solicitudMaterialAlmacenVo.getCantidadSolicitada());
            solicitudMaterial.setFechaSolicita(solicitudMaterialAlmacenVo.getFechaSolicita());
            solicitudMaterial.setFechaRequerida(solicitudMaterialAlmacenVo.getFechaRequiere());
            solicitudMaterial.setObservacion(solicitudMaterialAlmacenVo.getObservacion());
            solicitudMaterial.setTelefono(solicitudMaterialAlmacenVo.getTelefono());
            solicitudMaterial.setUsuarioRecoge(solicitudMaterialAlmacenVo.getUsuarioRecoge());
            if (solicitudMaterialAlmacenVo.getId() == 0) {
                solicitudMaterial.setFolio(folioRemote.traerFolioMesAnio(Constantes.FOLIO_VALE_SALIDA, campo));
                solicitudMaterial.setGenero(new Usuario(sesion));
                solicitudMaterial.setFechaGenero(new Date());
                solicitudMaterial.setHoraGenero(new Date());
                solicitudMaterial.setEliminado(Constantes.NO_ELIMINADO);
                //
                create(solicitudMaterial);
                // generar el registro en autorizaciones
                estadoAprobacionSolicitudLocal.guardar(solicitudMaterial.getId(), sesion, sesion, SolicitudMaterialEstadoEnum.SOLICITUD_CREADA.getId());
            } else {
                solicitudMaterial.setModifico(new Usuario(sesion));
                solicitudMaterial.setFechaModifico(new Date());
                solicitudMaterial.setHoraModifico(new Date());
                //
                edit(solicitudMaterial);
            }

            //agegar el material
            double total = 0;
            if (!solicitudMaterialAlmacenVo.getMateriales().isEmpty()) {
                //
                for (DetalleSolicitudMaterialAlmacenVo materiale : solicitudMaterialAlmacenVo.getMateriales()) {
                    if (materiale.getId() == 0) {
                        detalleSolicitudMaterialLocal.guardar(solicitudMaterial.getId(), materiale, sesion);
                    } else {
                        detalleSolicitudMaterialLocal.modificar(solicitudMaterial.getId(), materiale, sesion);
                    }

                    total += materiale.getCantidad();
                }
                solicitudMaterial.setCantidadSolicitada(total);
                //
                edit(solicitudMaterial);
            }
            return solicitudMaterial.getId();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return Constantes.CERO;
    }

    
    public void solicitarMateriales(int idSolicitudMaterial, String idAutoriza, String sesion) {
        SolicitudMaterialAlmacenVo ssAlmacenVo = solicitudesPorId(idSolicitudMaterial);
        notificacionInventarioLocal.enviarCorreoAutorizarMaterial(ssAlmacenVo, idAutoriza);
        //
        //actualizar
        InvEstadoAprobacionSolicitud estadoAprobacionSolicitud = estadoAprobacionSolicitudLocal.traerSolicitudporStatus(idSolicitudMaterial, SolicitudMaterialEstadoEnum.SOLICITUD_CREADA.getId());
        estadoAprobacionSolicitud.setActual(Constantes.BOOLEAN_FALSE);
        estadoAprobacionSolicitud.setModifico(new Usuario(sesion));
        estadoAprobacionSolicitud.setFechaModifico(new Date());
        estadoAprobacionSolicitud.setHoraModifico(new Date());
        //
        estadoAprobacionSolicitudLocal.edit(estadoAprobacionSolicitud);

        estadoAprobacionSolicitudLocal.guardar(idSolicitudMaterial, sesion, idAutoriza, SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId());
        //
        InvSolicitudMaterial sm = find(idSolicitudMaterial);
        sm.setFechaSolicita(new Date());
        sm.setEstatus(new Estatus(SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId()));
        sm.setModifico(new Usuario(sesion));
        sm.setFechaModifico(new Date());
        sm.setHoraModifico(new Date());
        //
        edit(sm);

    }

    
    public void actualizarEstatus(int idSol, int status, String sesion) {
        InvSolicitudMaterial sm = find(idSol);
        sm.setEstatus(new Estatus(status));
        sm.setModifico(new Usuario(sesion));
        sm.setFechaModifico(new Date());
        sm.setHoraModifico(new Date());
        //
        edit(sm);
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
        smav.setIdSolicita(((String) objs[14]));
        smav.setDevuelta((((Long) objs[15]) > Constantes.CERO) ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        smav.setUsuarioRecibeMaterial(((String) objs[16]));
        smav.setIdStatus(((Integer) objs[17]));
        smav.setStatus(((String) objs[18]));
        smav.setFechaEntrega(((Date) objs[19]));
        smav.setHoraEntrega(((Date) objs[20]));
        //
        smav.setMateriales(new ArrayList<>());
        smav.setMateriales(detalleSolicitudMaterialLocal.traerPorSolicitudId(smav.getId()));
        return smav;
    }

    
    public void eliminar(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo, String sesion) {
        InvSolicitudMaterial solicitudMaterial = find(solicitudMaterialAlmacenVo.getId());
        solicitudMaterial.setEliminado(Constantes.BOOLEAN_TRUE);
        solicitudMaterial.setModifico(new Usuario(sesion));
        solicitudMaterial.setFechaModifico(new Date());
        solicitudMaterial.setHoraModifico(new Date());
        edit(solicitudMaterial);
        //
        for (DetalleSolicitudMaterialAlmacenVo materiale : solicitudMaterialAlmacenVo.getMateriales()) {
            detalleSolicitudMaterialLocal.eliminar(materiale.getId(), sesion);
        }
    }

    
    public void actualizarUsuarioRecibeMaterial(SolicitudMaterialAlmacenVo ssVo, String sesion) {
        InvSolicitudMaterial sm = find(ssVo.getId());
        sm.setFechaEntrega(new Date());
        sm.setHoraEntrega(new Date());
        sm.setObservacion(ssVo.getObservacion());
        sm.setUsuarioRecibeMaterial(ssVo.getUsuarioRecibeMaterial());
        sm.setModifico(new Usuario(sesion));
        sm.setFechaModifico(new Date());
        sm.setHoraModifico(new Date());
        //
        edit(sm);
    }

    
    public List<SolicitudMaterialAlmacenVo> traerSolicitudesPorCampo(int idCampo, Date inicio, Date fin, int idStatus) {
        String c = consulta()
                + " where ism.ap_campo  = " + idCampo;
        if (inicio != null && fin != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
            c += " and ism.fecha_requerida between '" + sdf.format(inicio) + "' and '" + sdf.format(fin) + "'";
        }

        if (idStatus > Constantes.CERO) {
            c += " and ism.estatus = " + idStatus;
        }
        c += " and ism.eliminado = false "
                + " order by ism.id desc ";
        //
        List<Object[]> objects = em.createNativeQuery(c).getResultList();
        List<SolicitudMaterialAlmacenVo> lista = new ArrayList<>();
        for (Object[] object : objects) {
            lista.add(cast(object));
        }
        return lista;
    }

}
