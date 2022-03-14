/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgDetalleRutaCiudad;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.SiCiudad;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgDetalleRutaCiudadImpl extends AbstractFacade<SgDetalleRutaCiudad> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgDetalleRutaCiudadImpl() {
	super(SgDetalleRutaCiudad.class);
    }
    @Inject
    private SiCiudadImpl siCiudadRemote;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;    
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;

    //
    
    public List<SgDetalleRutaTerrestreVo> traerDetalleRutaPorRuta(int idSgRutaTerrestre, boolean eliminado) {
	clearQuery();

	query.append("SELECT drt.ID, "); //0
	query.append("drt.SG_RUTA_TERRESTRE, "); //1
	query.append("drt.si_ciudad , "); //2
	query.append("c.NOMBRE, "); //3
	query.append("drt.destino, "); //4
	query.append("c.LATITUD, "); //5
	query.append("c.LONGITUD "); //6
	query.append("FROM sg_detalle_ruta_ciudad drt, si_ciudad c ");
	query.append(" WHERE drt.SG_RUTA_TERRESTRE = ").append(idSgRutaTerrestre);
	query.append(" AND drt.eliminado = '").append(eliminado).append("' ");
	query.append(" AND drt.si_ciudad = c.ID ");
	query.append(" ORDER BY drt.si_ciudad asc");

	List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	List<SgDetalleRutaTerrestreVo> list = new ArrayList<SgDetalleRutaTerrestreVo>();

	for (Object[] objects : lo) {
	    list.add(castDetalleRutaCiudad(objects));
	}
	return list;
    }

    private SgDetalleRutaTerrestreVo castDetalleRutaCiudad(Object[] objects) {
	SgDetalleRutaTerrestreVo stvo = new SgDetalleRutaTerrestreVo();
	stvo.setId((Integer) objects[0]);
	stvo.setIdSgRutaTerrestre((Integer) objects[1]);
	stvo.setIdCiudad((Integer) objects[2]);
	stvo.setCiudad((String) objects[3]);
	stvo.setDestino((Boolean) objects[4]);
	stvo.setLatitud((String) objects[5]);
	stvo.setLongitud((String) objects[6]);
	return stvo;
    }

    
    public SgDetalleRutaTerrestreVo buscarRutaCiudadPorOficinaCiudad(int idOficina, int idCiudad) {
	clearQuery();
	try {
	    query.append("SELECT drt.ID, "); //0
	    query.append("drt.SG_RUTA_TERRESTRE, "); //1
	    query.append("drt.si_ciudad , "); //2
	    query.append("c.NOMBRE, "); //3
	    query.append("drt.destino, "); //3
	    query.append("c.LATITUD, "); //5
	    query.append("c.LONGITUD "); //6
	    query.append("FROM sg_detalle_ruta_ciudad drt, si_ciudad c, sg_ruta_terrestre rt ");
	    query.append(" WHERE rt.sg_oficina = ").append(idOficina);
	    query.append(" and drt.si_ciudad = ").append(idCiudad);
	    query.append(" and drt.sg_ruta_terrestre = rt.id ");
	    query.append(" AND drt.eliminado = 'False'");
	    query.append(" AND drt.si_ciudad = c.ID ");
	    Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    return castDetalleRutaCiudad(obj);
	} catch (NoResultException e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public SgDetalleRutaTerrestreVo buscarDetalleRutaCiudadDestinoPorRuta(int idRuta) {
	clearQuery();
	try {
	    query.append("SELECT drt.ID, "); //0
	    query.append("drt.SG_RUTA_TERRESTRE, "); //1
	    query.append("drt.si_ciudad , "); //2
	    query.append("c.NOMBRE, "); //3
	    query.append("drt.destino, "); //4
	    query.append("c.LATITUD, "); //5
	    query.append("c.LONGITUD "); //6
	    query.append("FROM sg_detalle_ruta_ciudad drt, si_ciudad c, sg_ruta_terrestre rt ");
	    query.append(" WHERE rt.id = ").append(idRuta);
	    query.append(" and drt.sg_ruta_terrestre = rt.id ");
	    query.append(" AND drt.eliminado = 'False'");
	    query.append(" AND drt.destino = '").append(Constantes.BOOLEAN_TRUE).append("'");
	    query.append(" AND drt.si_ciudad = c.ID ");
	    Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    return castDetalleRutaCiudad(obj);
	} catch (NoResultException e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public void guardarDetalleRuta(Usuario usuario, int idCiudad, int idRuta) {
	try {
	    SgDetalleRutaCiudad sgDetalleRutaCiudad = new SgDetalleRutaCiudad();
	    sgDetalleRutaCiudad.setDestino(Constantes.BOOLEAN_TRUE);
	    sgDetalleRutaCiudad.setSiCiudad(new SiCiudad(idCiudad));
	    sgDetalleRutaCiudad.setSgRutaTerrestre(new SgRutaTerrestre(idRuta));
	    sgDetalleRutaCiudad.setEliminado(Constantes.NO_ELIMINADO);
	    sgDetalleRutaCiudad.setGenero(usuario);
	    sgDetalleRutaCiudad.setFechaGenero(new Date());
	    sgDetalleRutaCiudad.setHoraGenero(new Date());
	    create(sgDetalleRutaCiudad);
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(ex, "Ocurrio un erreor");
	}
    }

    
    public void eliminarRuta(Usuario usuario, List<SgDetalleRutaTerrestreVo> l) {
	for (SgDetalleRutaTerrestreVo sgDetalleRutaTerrestre : l) {
	    try {
		SgDetalleRutaCiudad sgDetalleRutaCiudad = find(sgDetalleRutaTerrestre.getId());
		sgDetalleRutaCiudad.setEliminado(Constantes.BOOLEAN_TRUE);
		sgDetalleRutaCiudad.setModifico(usuario);
		sgDetalleRutaCiudad.setFechaModifico(new Date());
		sgDetalleRutaCiudad.setHoraModifico(new Date());
		edit(sgDetalleRutaCiudad);		
		sgEstadoSemaforoRemote.eliminarEstadoSemaforo(usuario.getId(), sgDetalleRutaTerrestre.getIdSgRutaTerrestre());
	    } catch (Exception ex) {
		Logger.getLogger(SgDetalleRutaCiudadImpl.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    
    public List<SgDetalleRutaTerrestreVo> detallesRutasCiudades(List<RutaTerrestreVo> listRuta) {
	clearQuery();
	List<SgDetalleRutaTerrestreVo> listDetalle = new ArrayList<SgDetalleRutaTerrestreVo>();
	try {
	    query.append("SELECT drt.ID, "); //0
	    query.append("drt.SG_RUTA_TERRESTRE, "); //1
	    query.append("drt.si_ciudad , "); //2
	    query.append("c.NOMBRE, "); //3
	    query.append("drt.destino, "); //4
	    query.append("'', ''");
	    query.append("FROM sg_detalle_ruta_ciudad drt, si_ciudad c, sg_ruta_terrestre rt ");
	    query.append(" WHERE rt.id IN(").append(Constantes.CERO);
	    for (RutaTerrestreVo vo : listRuta) {
		query.append(",").append(vo.getId());
	    }
	    query.append(")");
	    query.append(" and drt.sg_ruta_terrestre = rt.id ");
	    query.append(" AND drt.eliminado = 'False'");
	    query.append(" AND drt.destino = '").append(Constantes.BOOLEAN_TRUE).append("'");
	    query.append(" AND drt.si_ciudad = c.ID ");
	    List<Object[]> result = em.createNativeQuery(this.query.toString()).getResultList();
	    if (result != null) {
		for (Object[] obj : result) {
		    listDetalle.add(castDetalleRutaCiudad(obj));
		}
	    } else {
		listDetalle = null;
	    }
	} catch (Exception e) {
	    e.getMessage();
	}
	return listDetalle;
    }
}
