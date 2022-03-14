/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiAdjunto;
import sia.modelo.SiIncidencia;
import sia.modelo.SiIncidenciaAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.notificaciones.sistema.impl.ServicioNotificacionSistemaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.TicketEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiIncidenciaAdjuntoImpl extends AbstractFacade<SiIncidenciaAdjunto>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiIncidenciaAdjuntoImpl() {
        super(SiIncidenciaAdjunto.class);
    }
    @Inject
    ServicioNotificacionSistemaImpl notificacionSistemaRemote;
    @Inject
    SiIncidenciaImpl incidenciaLocal;
    @Inject
    SiAdjuntoImpl adjuntoRemote;

    @Inject
    SiIncidenciaMovimientoImpl incidenciaMovimientoLocal;

    
    public List<AdjuntoVO> traerArchivoPorIncidencia(int idIncidencia) {
        clearQuery();
        query.append("select a.id, a.nombre, a.url, a.tipo_archivo, a.peso, a.uuid, ia.id from si_incidencia_adjunto ia");
        query.append("      inner join si_adjunto a on ia.si_adjunto = a.id ");
        query.append("  where ia.si_incidencia = ").append(idIncidencia);
        query.append("  and ia.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<AdjuntoVO> la = new ArrayList<AdjuntoVO>();
        for (Object[] objects : lo) {
            la.add(castAdjunto(objects));
        }
        return la;
    }

    private AdjuntoVO castAdjunto(Object[] objects) {
        AdjuntoVO adjuntoVO = new AdjuntoVO();
        adjuntoVO.setId((Integer) objects[0]);
        adjuntoVO.setNombre((String) objects[1]);
        adjuntoVO.setUrl((String) objects[2]);
        adjuntoVO.setTipoArchivo((String) objects[3]);
        adjuntoVO.setPeso((String) objects[4]);
        adjuntoVO.setUuid((String) objects[5]);
        adjuntoVO.setIdTabla((Integer) objects[6]);
        return adjuntoVO;
    }

    
    public boolean agregarArchivoIncidencia(int idIncidencia, String sesion, int siAdjunto) {
        boolean v;
        try {
            SiIncidenciaAdjunto si = new SiIncidenciaAdjunto();
            si.setSiIncidencia(new SiIncidencia(idIncidencia));
            si.setSiAdjunto(new SiAdjunto(siAdjunto));
            si.setGenero(new Usuario(sesion));
            si.setFechaGenero(new Date());
            si.setHoraGenero(new Date());
            si.setEliminado(Constantes.NO_ELIMINADO);
            create(si);
            // Notificacion de usuarios
            IncidenciaVo incidenciaVo = incidenciaLocal.buscarPorId(idIncidencia);
            //
            notificacionSistemaRemote.enviarEvidenciaIncidencia(incidenciaVo);
            //
            incidenciaMovimientoLocal.gurdar(si.getId(), TicketEstadoEnum.CERRADO.getId(), sesion, "Adjunto");
            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No se guardo el archivo : : : : : " + e.getMessage());
            v = false;
        }
        return v;
    }

    
    public void eliminarRelacion(int idInciAdj, String sesion) {
        SiIncidenciaAdjunto siIncidenciaAdjunto = find(idInciAdj);
        //
        try {
            adjuntoRemote.eliminarArchivo(siIncidenciaAdjunto.getSiAdjunto().getId(), sesion);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        //
        siIncidenciaAdjunto.setSiAdjunto(null);
        siIncidenciaAdjunto.setModifico(new Usuario(sesion));
        siIncidenciaAdjunto.setFechaModifico(new Date());
        siIncidenciaAdjunto.setHoraModifico(new Date());
        siIncidenciaAdjunto.setEliminado(Constantes.ELIMINADO);
        edit(siIncidenciaAdjunto);
    }
}
