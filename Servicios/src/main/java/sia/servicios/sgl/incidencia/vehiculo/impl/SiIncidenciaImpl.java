/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.Estatus;
import sia.modelo.Gerencia;
import sia.modelo.Prioridad;
import sia.modelo.SiCategoriaIncidencia;
import sia.modelo.SiIncidencia;
import sia.modelo.SiLocalizacion;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.notificaciones.sistema.impl.ServicioNotificacionSistemaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiLocalizacionImpl;
import sia.servicios.sistema.impl.SiNivelImpl;
import sia.util.TicketEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiIncidenciaImpl extends AbstractFacade<SiIncidencia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiIncidenciaImpl() {
        super(SiIncidencia.class);
    }
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private FolioImpl folioRemote;
    @Inject
    private SiTagImpl siTagLocal;
    @Inject
    private SiIncidenciaTagImpl siIncidenciaTagLocal;
    @Inject
    private SiLocalizacionImpl siLocalizacionLocal;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private NotificacionViajeImpl notificacionViajeRemote;
    @Inject
    SiIncidenciaAdjuntoImpl incidenciaAdjuntoLocal;
    @Inject
    SiAdjuntoImpl siAdjuntoRemote;
    @Inject
    SiIncidenciaMovimientoImpl incidenciaMovimientoLocal;
    @Inject
    ServicioNotificacionSistemaImpl notificacionSistemaRemote;
    @Inject
    SiNivelImpl nivelLocal;

    
    public int guardar(String titulo, String descripcion, int prioridad, int gerencia, int estado,
            String palabaClave, String sesion, int localizacion) throws Exception {
        try {
            SiIncidencia siIncidencia = new SiIncidencia();
            siIncidencia.setCodigo(folioNumerico());
            siIncidencia.setTitulo(titulo);
            siIncidencia.setDescripcion(descripcion);
            siIncidencia.setPrioridad(new Prioridad(prioridad));
            siIncidencia.setGerencia(new Gerencia(gerencia));
            siIncidencia.setEstatus(new Estatus(estado));
            siIncidencia.setSiNivel(nivelLocal.bucarPorCodigo(Constantes.PRIMER_NIVEL));
            siIncidencia.setSiLocalizacion(localizacion == Constantes.CERO ? null : new SiLocalizacion(localizacion));
            //
            siIncidencia.setGenero(new Usuario(sesion));
            siIncidencia.setFechaGenero(new Date());
            siIncidencia.setHoraGenero(new Date());
            siIncidencia.setEliminado(Constantes.NO_ELIMINADO);
            create(siIncidencia);
            //
            //Guarda los tag
            if (!palabaClave.isEmpty()) {
                for (String tag : separarCadena(palabaClave)) {
                    int idTag = siTagLocal.guardar(sesion, tag);
                    //
                    siIncidenciaTagLocal.guardar(sesion, siIncidencia.getId(), idTag);
                }
            }
            return siIncidencia.getId();
        } catch (Exception e) {
            throw e;
        }
    }

    private String[] separarCadena(String palabraClave) {
        String[] cad = palabraClave.split(",");
        return cad;
    }

    private String generaCodigo(int gerencia) {
        String cad;
        GerenciaVo gerenciaVo = gerenciaRemote.buscarPorId(gerencia);
        if (!gerenciaVo.getAbrev().isEmpty() && gerenciaVo.getAbrev() != null) {
            cad = gerenciaVo.getAbrev().substring(0, 3);
        } else {
            cad = gerenciaVo.getNombre().substring(0, 3);
        }
        return cad;
    }

    private String folio() {
        return folioRemote.traerFolioAnio("INCIDENCIA", Constantes.AP_CAMPO_DEFAULT);
    }

    private int folioNumerico() {
        return folioRemote.traerFolioPorCampo("INCIDENCIA", Constantes.AP_CAMPO_DEFAULT);
    }
    
    
    public IncidenciaVo buscarPorId(int idIncidencia) {
        IncidenciaVo incidenciaVo = new IncidenciaVo();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(consulta())
                    .append(" where i.id = ").append(idIncidencia);
            //
            Object[] objects = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            incidenciaVo = castIncidencia(objects);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No se encontró la incidencia -  . . . " + e.getMessage());
            incidenciaVo = null;
        }
        return incidenciaVo;
    }

    private IncidenciaVo castIncidencia(Object[] objects) {
        IncidenciaVo incidenciaVo = new IncidenciaVo();
        incidenciaVo.setIdIncidencia((Integer) objects[0]);
        incidenciaVo.setTitulo((String) objects[1]);
        incidenciaVo.setDescripcion((String) objects[2]);
        incidenciaVo.setCodigo(((BigDecimal) objects[3]).toString());
        incidenciaVo.setFechaGenero((Date) objects[4]);
        incidenciaVo.setGerencia((String) objects[5]);
        incidenciaVo.setEstado((String) objects[6]);
        incidenciaVo.setPrioridad((String) objects[7]);
        incidenciaVo.getGrMapaGPSVO().setLonCoord((String) objects[8]);
        incidenciaVo.getGrMapaGPSVO().setLatCoord((String) objects[9]);
        //
        incidenciaVo.setIdCategoriaIncidencia(objects[10] != null ? (Integer) objects[10] : Constantes.CERO);
        incidenciaVo.setCategoriaIncidencia((String) objects[11]);
        incidenciaVo.setIdGenero((String) objects[12]);
        incidenciaVo.setGenero((String) objects[13]);
        incidenciaVo.setCodigoCategoria((String) objects[14]);
        incidenciaVo.setIdCampo(objects[15] != null ? (Integer) objects[15] : Constantes.CERO);
        incidenciaVo.setCampo((String) objects[16]);
        incidenciaVo.setSolucion((String) objects[17]);
        incidenciaVo.setIdAsignadoA((String) objects[18]);
        incidenciaVo.setAsignado((String) objects[19]);
        incidenciaVo.setCorreoGenero((String) objects[20]);
        incidenciaVo.setHoraGenero((Date) objects[21]);
        incidenciaVo.setIdEstado(objects[22] != null ? (Integer) objects[22] : Constantes.CERO);
        incidenciaVo.setCorreoAsignado((String) objects[23]);
        incidenciaVo.setIdNivel(objects[24] != null ? (Integer) objects[24] : Constantes.CERO);
        incidenciaVo.setNivel((String) objects[25]);
        incidenciaVo.setCodigoNivel((String) objects[26]);
        incidenciaVo.setEscalado((Boolean) objects[27]);
        return incidenciaVo;
    }

    
    public void guardarIncidenciaLocalizacion(JsonObject ubicacion, String sesion) {
        try {
            int idLoc;
            Usuario usuario = usuarioRemote.findRH(sesion.replace("\"", ""));
            if (ubicacion != null) {
                idLoc = siLocalizacionLocal.guardar(usuario.getId(), ubicacion);
                String titulo = "Evento en viaje";
                String mensaje = "Favor de comunicarse a la brevedad con " + usuario.getNombre();

                guardar(titulo, mensaje, Constantes.UNO, usuario.getGerencia().getId(), 140, "viaje", usuario.getId(), idLoc);

                notificacionViajeRemote.mensajeDirecto("Evento en viaje", mensaje, (ubicacion.get("longitud").getAsString()), ubicacion.get("latitud").getAsString());
            }
        } catch (Exception ex) {
            Logger.getLogger(SiIncidenciaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public List<IncidenciaVo> traerPorUsuario(String sesion, int status) {
        StringBuilder sb = new StringBuilder();
        List<IncidenciaVo> incidencias = new ArrayList<IncidenciaVo>();
        try {
            sb.append(consulta())
                    .append(" where i.genero = '").append(sesion).append("'")
                    .append(" and i.estado = ").append(status)
                    .append(" and i.eliminado = false ")
                    .append(" order by i.id desc ");
            //
            List<Object[]> objects = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] object : objects) {
                incidencias.add(castIncidencia(object));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No se encontró la incidencia -  . . . " + e.getMessage());
        }
        return incidencias;
    }

    private String consulta() {
        StringBuilder sb = new StringBuilder();
        sb.append("select i.id, i.titulo, i.descripcion, i.codigo, i.fecha_genero, g.nombre, e.nombre,\n"
                + " p.nombre, l.longitud, l.latitud, ci.id, ci.nombre, ug.id, ug.nombre, i.codigo_categoria, \n "
                + " ac.id, ac.nombre, i.solucion, uas.id, uas.nombre, ug.email, i.hora_genero, e.id, uas.email, "
                + " niv.id, niv.nombre, niv.codigo, i.escalado \n"
                + "from si_incidencia i \n"
                + "      inner join gerencia g on i.gerencia = g.id \n"
                + "      inner join prioridad p on i.prioridad = p.id \n"
                + "      inner join estatus e on i.estado = e.id \n"
                + "      inner join si_categoria_incidencia ci on i.si_categoria_incidencia = ci.id\n"
                + "      inner join usuario ug on i.genero = ug.id \n"
                + "      inner join ap_campo ac on i.ap_campo = ac.id	\n"
                + "      left join usuario uas on i.asignado_a = uas.id\n"
                + "      left join si_localizacion l on i.si_localizacion = l.id \n"
                + "      inner join si_nivel niv on i.si_nivel = niv.id ");

        return sb.toString();
    }

    
    public void guardar(IncidenciaVo incidenciaVo, List<AdjuntoVO> adjuntos, Usuario sesion) {
        try {
            SiIncidencia siIncidencia = new SiIncidencia();
            siIncidencia.setCodigo(folioNumerico());
            siIncidencia.setCodigoCategoria(incidenciaVo.getCodigoCategoria());
            siIncidencia.setTitulo(incidenciaVo.getTitulo());
            siIncidencia.setDescripcion(incidenciaVo.getDescripcion());
            siIncidencia.setPrioridad(new Prioridad(incidenciaVo.getIdPrioridad()));
            siIncidencia.setGerencia(new Gerencia(incidenciaVo.getIdGerencia()));
            siIncidencia.setEstatus(new Estatus(TicketEstadoEnum.NUEVO.getId()));

            siIncidencia.setSiNivel(nivelLocal.bucarPorCodigo(Constantes.PRIMER_NIVEL));
            siIncidencia.setEscalado(Constantes.BOOLEAN_FALSE);
            //
            siIncidencia.setApCampo(new ApCampo(incidenciaVo.getIdCampo()));
            siIncidencia.setCodigoCategoria(incidenciaVo.getCodigoCategoria());
            siIncidencia.setSiCategoriaIncidencia(new SiCategoriaIncidencia(incidenciaVo.getIdCategoriaIncidencia()));
            //
            siIncidencia.setGenero(sesion);
            siIncidencia.setFechaGenero(new Date());
            siIncidencia.setHoraGenero(new Date());
            siIncidencia.setEliminado(Constantes.NO_ELIMINADO);
            siIncidencia.setDuracion(15);
            create(siIncidencia);
            //
            for (AdjuntoVO adjunto : adjuntos) {
                incidenciaAdjuntoLocal.agregarArchivoIncidencia(siIncidencia.getId(), sesion.getId(), adjunto.getId());
            }
            //crear movimiento
            incidenciaMovimientoLocal.gurdar(siIncidencia.getId(), TicketEstadoEnum.NUEVO.getId(), sesion.getId(), "Nuevo");
            //envia la notificacion
            incidenciaVo.setCodigo(siIncidencia.getCodigo()+"");
            notificacionSistemaRemote.enviarIncidencia(incidenciaVo, sesion);
            //
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public void reenviarIncidencia(IncidenciaVo incidenciaVo, String complemento, Usuario usuario) {
        //
        notificacionSistemaRemote.reenviarIncidencia(incidenciaVo, complemento, usuario);
        //
        //
        SiIncidencia siIncidencia = find(incidenciaVo.getIdIncidencia());
        StringBuilder sb = new StringBuilder();
        String desc = siIncidencia.getDescripcion() + " Complemento: " + complemento + " Fecha: " + Constantes.FMT_ddMMyyy.format(new Date()) + " Hora: " + Constantes.FMT_HHmmss.format(new Date());
        siIncidencia.setDescripcion(desc);
        siIncidencia.setModifico(usuario);
        siIncidencia.setFechaModifico(new Date());
        siIncidencia.setHoraModifico(new Date());
        //
        edit(siIncidencia);
        //
        incidenciaMovimientoLocal.gurdar(siIncidencia.getId(), siIncidencia.getEstatus().getId(), usuario.getId(), "Complemento");
    }
    
    
    public void cerrarIncidencia(IncidenciaVo incidenciaVo, String motivoCierre, Usuario usuario) {
        this.cerrarIncidencia(incidenciaVo, motivoCierre, 0, usuario);     
    }

    
    public void cerrarIncidencia(IncidenciaVo incidenciaVo, String motivoCierre, int duracion, Usuario usuario) {
        //
        notificacionSistemaRemote.cierreIncidencia(incidenciaVo, motivoCierre, usuario);
        //
        SiIncidencia siIncidencia = find(incidenciaVo.getIdIncidencia());
        siIncidencia.setEstatus(new Estatus(TicketEstadoEnum.CERRADO.getId()));
        siIncidencia.setMotivoCierre(motivoCierre);
        siIncidencia.setDuracion(duracion);
        siIncidencia.setModifico(usuario);
        siIncidencia.setFechaModifico(new Date());
        siIncidencia.setHoraModifico(new Date());
        //
        edit(siIncidencia);
        //
        incidenciaMovimientoLocal.gurdar(siIncidencia.getId(), siIncidencia.getEstatus().getId(), usuario.getId() , "Cerrado");
    }

    
    public List<IncidenciaVo> traerPorStatus(int status) {
        StringBuilder sb = new StringBuilder();
        List<IncidenciaVo> incidencias = new ArrayList<IncidenciaVo>();
        try {
            sb.append(consulta())
                    .append(" where i.estado = ").append(status)
                    .append(" and i.eliminado = false ")
                    .append(" order by i.id desc ");
            //
            List<Object[]> objects = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] object : objects) {
                incidencias.add(castIncidencia(object));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No se encontró la incidencia -  . . . " + e.getMessage());
        }
        return incidencias;
    }

    
    public void asignarIncidencia(IncidenciaVo incidenciaVo, String IdAsignado, Usuario sesion) {
        //        
        SiIncidencia siIncidencia = find(incidenciaVo.getIdIncidencia());
        UsuarioVO usuarioVo = usuarioRemote.findById(IdAsignado);
        notificacionSistemaRemote.asignarIncidencia(incidenciaVo, usuarioVo, sesion);
        //
        //
        siIncidencia.setAsignadoA(new Usuario(usuarioVo.getId()));
        siIncidencia.setEstatus(new Estatus(TicketEstadoEnum.ASIGNADO.getId()));
        siIncidencia.setModifico(sesion);
        siIncidencia.setFechaModifico(new Date());
        siIncidencia.setHoraModifico(new Date());
        //
        edit(siIncidencia);
        //
        incidenciaMovimientoLocal.gurdar(siIncidencia.getId(), TicketEstadoEnum.ASIGNADO.getId(), sesion.getId(), "Asignado");
    }

    
    public void finalizarIncidencia(IncidenciaVo incidenciaVo, String solucion, int duracion, Usuario sesion) {
        //        
        notificacionSistemaRemote.finalizarIncidencia(incidenciaVo, sesion);
        //
        SiIncidencia siIncidencia = find(incidenciaVo.getIdIncidencia());
        //
        siIncidencia.setEstatus(new Estatus(TicketEstadoEnum.SOLUCIONADO.getId()));
        siIncidencia.setSolucion(solucion);
        siIncidencia.setDuracion(duracion);
        siIncidencia.setModifico(sesion);
        siIncidencia.setFechaModifico(new Date());
        siIncidencia.setHoraModifico(new Date());
        //
        edit(siIncidencia);
        //
        incidenciaMovimientoLocal.gurdar(siIncidencia.getId(), TicketEstadoEnum.SOLUCIONADO.getId(), sesion.getId(),"Finalizado");
    }

    
    public boolean validaCodigo(String tabla, String campo, String codigo) {
        try {
            String c = "select * from " + tabla + " where " + campo + " = '" + codigo + "'";
            //
            return em.createNativeQuery(c).getSingleResult() != null;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }
    }

    
    public void confirmarSolucionIncidencia(IncidenciaVo incidenciaVo, String userId) {
        SiIncidencia incidencia = find(incidenciaVo.getIdIncidencia());
        incidencia.setEstatus(new Estatus(TicketEstadoEnum.CERRADO.getId()));
        incidencia.setModifico(new Usuario(userId));
        incidencia.setFechaModifico(new Date());
        incidencia.setHoraModifico(new Date());
        edit(incidencia);
        //
        incidenciaMovimientoLocal.gurdar(incidencia.getId(), TicketEstadoEnum.CERRADO.getId(), userId, "Confirmar solución");
    }

    
    public void cambiarEstadoIncidencia(IncidenciaVo incidenciaVo, String userId) {
        SiIncidencia incidencia = find(incidenciaVo.getIdIncidencia());
        incidencia.setEstatus(new Estatus(TicketEstadoEnum.ASIGNADO.getId()));
        incidencia.setModifico(new Usuario(userId));
        incidencia.setFechaModifico(new Date());
        incidencia.setHoraModifico(new Date());
        edit(incidencia);
        //notifica 
        notificacionSistemaRemote.reAsignarIncidencia(incidenciaVo);
        //
        incidenciaMovimientoLocal.gurdar(incidencia.getId(), TicketEstadoEnum.ASIGNADO.getId(), userId, "Re-asignar incidencia");
    }

    
    public void escalarIncidencia(IncidenciaVo incidenciaVo, String sesion) {
        SiIncidencia incidencia = find(incidenciaVo.getIdIncidencia());
        incidencia.setMotivoEscala(incidenciaVo.getMotivoEscala());
        incidencia.setSiNivel(nivelLocal.bucarPorCodigo(Constantes.SEGUNDO_NIVEL));
        incidencia.setModifico(new Usuario(sesion));
        incidencia.setFechaModifico(new Date());
        incidencia.setHoraModifico(new Date());
        edit(incidencia);
        //notifica cambio de nivel
        notificacionSistemaRemote.enviarEscalaTicket(incidenciaVo);
        //
        incidenciaMovimientoLocal.gurdar(incidencia.getId(), incidencia.getEstatus().getId(), sesion, "Escalado");
    }

    
    public void aceptarCambioNivelIncidencia(IncidenciaVo incidenciaVo, String userId) {

        SiIncidencia incidencia = find(incidenciaVo.getIdIncidencia());
        incidencia.setEscalado(Constantes.BOOLEAN_TRUE);
        incidencia.setModifico(new Usuario(userId));
        incidencia.setFechaModifico(new Date());
        incidencia.setHoraModifico(new Date());
        edit(incidencia);
        //notifica 
        notificacionSistemaRemote.enviarAceptacionEscalaTicket(incidenciaVo);
        //
        incidenciaMovimientoLocal.gurdar(incidencia.getId(), incidencia.getEstatus().getId(), userId, "Acepta escalado");
    }

    
    public void noAceptarCambioNivelIncidencia(IncidenciaVo incidenciaVo, String userId) {

        SiIncidencia incidencia = find(incidenciaVo.getIdIncidencia());
        incidencia.setSiNivel(nivelLocal.bucarPorCodigo(Constantes.PRIMER_NIVEL));
        incidencia.setEscalado(Constantes.BOOLEAN_FALSE);
        incidencia.setModifico(new Usuario(userId));
        incidencia.setFechaModifico(new Date());
        incidencia.setHoraModifico(new Date());
        edit(incidencia);
        //notifica 
        notificacionSistemaRemote.enviarNoAceptacionEscalaTicket(incidenciaVo);
        //
        incidenciaMovimientoLocal.gurdar(incidencia.getId(), incidencia.getEstatus().getId(), userId, "No acepta escalado");
    }
}
