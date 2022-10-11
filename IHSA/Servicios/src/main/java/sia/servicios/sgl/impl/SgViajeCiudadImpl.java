/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgViajeCiudad;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.ViajeDestinoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgViajeCiudadImpl extends AbstractFacade<SgViajeCiudad> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;       
    @Inject
    private SiCiudadImpl siCiudadRemote;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;    

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgViajeCiudadImpl() {
        super(SgViajeCiudad.class);
    }

    
    public boolean crear(Integer idSgSolicitudViaje, Integer idSiCiudad, String idUsuario) {
        try {
            SgViajeCiudad viajeCiudad = new SgViajeCiudad();
            viajeCiudad.setEliminado(Constantes.BOOLEAN_FALSE);
            viajeCiudad.setGenero(new Usuario(idUsuario));
            viajeCiudad.setFechaGenero(new Date());
            viajeCiudad.setHoraGenero(new Date());
            viajeCiudad.setSgSolicitudViaje(sgSolicitudViajeRemote.find(idSgSolicitudViaje));
            viajeCiudad.setSiCiudad(siCiudadRemote.find(idSiCiudad));

            create(viajeCiudad);

            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al guardar viaje ciudad " + e.getMessage());
            return false;
        }
    }

    
    public ViajeDestinoVo findDestinoSolicitudViaje(Integer idSolicitudViaje) {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.findDestinoSolicitudViaje()");
        try {
            clearQuery();
            appendQuery("Select vc.id,");//0
            appendQuery(" ciu.id, ");//1
            appendQuery(" ciu.NOMBRE,");//2
            appendQuery(" es.nombre,");//3
            appendQuery(" pa.NOMBRE ");//4
            appendQuery(" From SG_VIAJE_CIUDAD vc, SI_CIUDAD ciu,SI_ESTADO es,SI_PAIS pa");
            appendQuery(" Where vc.SG_SOLICITUD_VIAJE = ").append(idSolicitudViaje);
            appendQuery(" AND vc.SI_CIUDAD = ciu.ID");
            appendQuery(" AND vc.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND ciu.SI_estado = es.ID");
            appendQuery(" AND es.SI_PAIS = pa.ID");

            Object[] result = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

            ViajeDestinoVo destino = new ViajeDestinoVo();
            destino.setId((Integer) result[0]);
            destino.setIdCiudadDestino((Integer) result[1]);
            destino.setCiudadDestino((String) result[2]);
            destino.setEstadoDestino((String) result[3]);
            destino.setPaisDestino((String) result[4]);

            return destino;
        } catch (NoResultException nre) {
//            UtilLog4j.log.fatal(this, nre.getMessage());
//            nre.printStackTrace();
            return null;
        }
    }

    
    public boolean modificar(Integer idSolicitudViaje, Integer idSiCiudad, String idUsuario) {
        UtilLog4j.log.info(this, "modificarSolicitudCiudad");        
        try {
            ViajeDestinoVo vo = findDestinoSolicitudViaje(idSolicitudViaje);
            SgViajeCiudad viajeCiudad = find(vo.getId());            
            viajeCiudad.setModifico(new Usuario(idUsuario));
            viajeCiudad.setFechaModifico(new Date());
            viajeCiudad.setHoraModifico(new Date());            
            viajeCiudad.setSiCiudad(siCiudadRemote.find(idSiCiudad));
            edit(viajeCiudad);
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al modificar viaje ciudad " + e.getMessage());
            return false;
        }
    }

    
    public SgViajeCiudad findSgViajeCiudadBySgSolicitudViaje(int idSgSolicitudViaje) {
        try {
            return (SgViajeCiudad) em.createQuery("SELECT vc FROM SgViajeCiudad vc WHERE vc.sgSolicitudViaje.id = :idSgSolicitudViaje AND vc.eliminado = :eli")
                        .setParameter("eli", Constantes.BOOLEAN_FALSE)
                        .setParameter("idSgSolicitudViaje", idSgSolicitudViaje)
                        .getSingleResult();
        }
        catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    
    public void eliminarViajeCiudad(Integer idCiudadViaje, String idUsuario) {
        UtilLog4j.log.info(this, "modificarSolicitudCiudad");        
        try {
            
            SgViajeCiudad viajeCiudad = find(idCiudadViaje);            
            viajeCiudad.setModifico(new Usuario(idUsuario));
            viajeCiudad.setFechaModifico(new Date());
            viajeCiudad.setHoraModifico(new Date());            
            viajeCiudad.setEliminado(Constantes.ELIMINADO);
            edit(viajeCiudad);
            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al modificar viaje ciudad " + e.getMessage());
            
        }
    }
    
    
    public SgViajeCiudad crear(int idSiCiudad, String idUsuario) {
        SgViajeCiudad viajeCiudad = new SgViajeCiudad();
        try {
            viajeCiudad.setSiCiudad(siCiudadRemote.find(idSiCiudad));
            viajeCiudad.setEliminado(Constantes.BOOLEAN_FALSE);
            viajeCiudad.setGenero(new Usuario(idUsuario));
            viajeCiudad.setFechaGenero(new Date());
            viajeCiudad.setHoraGenero(new Date());
            create(viajeCiudad);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al guardar viaje ciudad " + e.getMessage());
        }
        return viajeCiudad;
    }
}
