/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.SgCambioItinerario;
import sia.modelo.SgItinerario;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.CambioItinerarioVO;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgCambioItinerarioImpl extends AbstractFacade<SgCambioItinerario> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private SgItinerarioImpl itinerarioService;
    @Inject
    private SgItinerarioImpl sgItinerarioRemote;
    @Inject
    private NotificacionViajeImpl notificacionViajeRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgCambioItinerarioImpl() {
        super(SgCambioItinerario.class);
    }

    
    public void update(SgCambioItinerario cambioItinerario, int idSgItinerario, String idUsuario) {

        cambioItinerario.setSgItinerario(this.sgItinerarioRemote.find(idSgItinerario));

        cambioItinerario.setModifico(new Usuario(idUsuario));
        cambioItinerario.setFechaModifico(new Date());
        cambioItinerario.setHoraModifico(new Date());

        super.edit(cambioItinerario);
    }

    
    public List<CambioItinerarioVO> getCambioItinerarioVOPorItinerario(Integer idItinerario) {
        List<CambioItinerarioVO> list = new ArrayList<>();
        clearQuery();
        query.append(" select cit.id, ");
        query.append(" cit.mensaje, ");
        query.append(" cit.historial ");
        query.append(" from sg_cambio_itinerario cit");
        query.append(" where cit.sg_itinerario =").append(idItinerario);
        query.append(" and (cit.historial ='").append(Constantes.BOOLEAN_FALSE).append("' ");
        query.append(" or cit.historial is null ) ");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        if (lo != null) {

            for (Object[] objects : lo) {
                list.add(castCambioItinerarioVO(objects));
            }
        }

        return list;
    }

    private CambioItinerarioVO castCambioItinerarioVO(Object[] obj) {

        CambioItinerarioVO vo = new CambioItinerarioVO();
        vo.setId((Integer) obj[0]);
        vo.setMensaje((String) obj[1]);
        vo.setHistorial((String) obj[2]);

        return vo;
    }

    /**
     * Creo: NLopez 24/10/2013
     *
     * @param idSolicitud
     * @param tipoViaje
     * @param mensaje
     * @param idUsuario
     */
    
    public void guardarJustificacionItinerario(int idSolicitud, String tipoViaje, String mensaje, String idUsuario, String correoSesion, String nombre) {

        SgCambioItinerario sgCambioItinerario = new SgCambioItinerario();

        //Enviar las notificaciones de cambio de itinerario
        SolicitudViajeVO solicitudViajeVO = sgSolicitudViajeRemote.buscarPorId(idSolicitud, Constantes.NO_ELIMINADO,Constantes.CERO);
        boolean v = notificacionViajeRemote.enviarCorreoSolicitudCambioItinerario(correoPara(idSolicitud, Constantes.ESTATUS_APROBAR), correoCopia(idSolicitud), tipoViaje, mensaje, solicitudViajeVO.getCodigo(), nombre, idSolicitud);
        if (v) {
            v = notificacionViajeRemote.enviarCorreoSolicitudCambioItinerarioGenero(correoSesion, tipoViaje, mensaje, solicitudViajeVO.getCodigo(), nombre, idSolicitud);
        }
        if (v) {
            ItinerarioCompletoVo it = itinerarioService.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitud, Boolean.valueOf(tipoViaje), Boolean.valueOf(Constantes.BOOLEAN_FALSE), Constantes.ORDENAR_POR_ID);
            SgItinerario sgIt = new SgItinerario();
            sgIt.setId(it.getId());

            sgCambioItinerario.setSgItinerario(sgIt);
            sgCambioItinerario.setGenero(new Usuario(idUsuario));
            sgCambioItinerario.setMensaje(mensaje);
            sgCambioItinerario.setFechaGenero(new Date());
            sgCambioItinerario.setHoraGenero(new Date());
            sgCambioItinerario.setEliminado(Constantes.NO_ELIMINADO);
            sgCambioItinerario.setHistorial(Constantes.BOOLEAN_FALSE);
        }
    }

    
    public int getTotalCambiosItinerario() {
        clearQuery();
        query.append(" select count(cit.id) ");
        query.append(" from sg_cambio_itinerario cit");
        query.append(" where ");
        query.append(" (cit.historial ='").append(Constantes.BOOLEAN_FALSE).append("' ");
        query.append(" or cit.historial is null ) ");

        Query q = em.createNativeQuery(query.toString());

        return ((Integer) q.getSingleResult()).intValue();
    }

    private String correoPara(int idSolicitud, int idEstatus) {
        String correo = "";
        try {
            EstatusAprobacionVO eavo = sgEstatusAprobacionRemote.buscarEstatusAprocionPorSolicitudEstatus(idSolicitud, idEstatus);
            if (eavo != null) {
                correo = eavo.getCorreoUsuario();
            }
        } catch (Exception e) {
            return "";
        }
        return correo;
    }

    private String correoCopia(int idSolicitud) {
        String correo = "";
        List<ViajeroVO> ls = sgViajeroRemote.getAllViajerosList(idSolicitud);
        for (ViajeroVO viajeroVO : ls) {
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
