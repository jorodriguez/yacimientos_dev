/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgViajeLugar;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.ViajeLugarVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgViajeLugarImpl extends AbstractFacade<SgViajeLugar>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgViajeLugarImpl() {
        super(SgViajeLugar.class);
    }    
    @Inject
    private SgLugarImpl sgLugarRemote;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;

    
    public void guardar(int idSolicitud, int idDestino, String idUsuario) {
        try {
            SgViajeLugar viajeLugar = new SgViajeLugar();
            viajeLugar.setEliminado(Constantes.BOOLEAN_FALSE);
            viajeLugar.setGenero(new Usuario(idUsuario));
            viajeLugar.setFechaGenero(new Date());
            viajeLugar.setHoraGenero(new Date());
            viajeLugar.setSgSolicitudViaje(sgSolicitudViajeRemote.find(idSolicitud));
            viajeLugar.setSgLugar(sgLugarRemote.find(idDestino));

            create(viajeLugar);

        } catch (Exception e) {
            UtilLog4j.log.error(e);

        }
    }

    
    public ViajeLugarVO buscarLugarDestinoSolicitudViaje(int idSolicitudViaje) {
        try {
            clearQuery();
            appendQuery("Select vl.id,");//0
            appendQuery(" lu.id, ");//1
            appendQuery(" lu.NOMBRE");//2
            appendQuery(" From SG_VIAJE_lugar vl, Sg_lugar lu");
            appendQuery(" Where vl.SG_SOLICITUD_VIAJE = ").append(idSolicitudViaje);
            appendQuery(" AND vl.Sg_lugar = lu.ID");
            appendQuery(" AND vl.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");



            Object[] result = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

            ViajeLugarVO destino = new ViajeLugarVO();
            destino.setIdViajeLugar((Integer) result[0]);
            destino.setIdLugar((Integer) result[1]);
            destino.setNombreLugar((String) result[2]);

            return destino;
        } catch (NoResultException nre) {
            UtilLog4j.log.error(nre);
            return null;
        }
    }

    
    public void eliminarViajeLugar(int idRutaViaje, String idUsuario) {
        System.out.println("modificarSolicitudCiudad");        
        try {

            SgViajeLugar viajeLugar = find(idRutaViaje);
            viajeLugar.setModifico(new Usuario(idUsuario));
            viajeLugar.setFechaModifico(new Date());
            viajeLugar.setHoraModifico(new Date());
            viajeLugar.setEliminado(Constantes.ELIMINADO);
            edit(viajeLugar);
            
        } catch (Exception e) {
            UtilLog4j.log.error(e);

        }
    }
}
