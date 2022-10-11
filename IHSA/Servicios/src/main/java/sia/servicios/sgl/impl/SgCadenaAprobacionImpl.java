/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.Gerencia;
import sia.modelo.SgCadenaAprobacion;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.CadenaAprobacionSolicitudVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgCadenaAprobacionImpl extends AbstractFacade<SgCadenaAprobacion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgTipoSolicitudViajeImpl tipoSolicitudService;
    @Inject
    private GerenciaImpl gerenciaService;
    @Inject
    private EstatusImpl estatusService;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCadenaAprobacionImpl() {
	super(SgCadenaAprobacion.class);
    }

    
    public List<SgCadenaAprobacion> traerCadenaAprobacionTipoViaje(SgTipoSolicitudViaje tipoSolicitud, boolean eliminado) {
	UtilLog4j.log.info(this, "Entrando a bucar cadenas de aprobacion del tipo de solicitud" + tipoSolicitud.getNombre());
	List<SgCadenaAprobacion> listReturn = null;
	if (tipoSolicitud != null) {
	    try {
		listReturn = em.createQuery("SELECT ca FROM SgCadenaAprobacion ca "
			+ "WHERE ca.eliminado = :eliminado And ca.sgTipoSolicitudViaje = :tipoSolicitudViaje ORDER BY ca.id ASC ").setParameter("tipoSolicitudViaje", tipoSolicitud).setParameter("eliminado", eliminado).getResultList();
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
	    }
	}
	return listReturn;
    }

    
    public SgCadenaAprobacion crearCadenaAprobacion(int idTipoSolicitud, int idGerencia, int idEstatus, Usuario usuarioGenero) {
	try {
	    SgCadenaAprobacion cad = new SgCadenaAprobacion();
	    cad.setEliminado(Constantes.BOOLEAN_FALSE);
	    cad.setGenero(usuarioGenero);
	    cad.setSgTipoSolicitudViaje(tipoSolicitudService.find(idTipoSolicitud));
	    cad.setEstatus(estatusService.find(idEstatus));
	    cad.setGerencia(gerenciaService.find(idGerencia));
	    super.create(cad);
	    return cad;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en crearCadenaAprobacion " + e.getMessage());
	    return null;
	}
    }

    
    public void modificarCadenaAprobacion(int idCadena, int idTipoSolicitud, int idGerencia, int idEstatus, Usuario usuarioModifico) {
	try {
	    SgCadenaAprobacion cad = find(idCadena);
	    
	    cad.setEliminado(Constantes.BOOLEAN_FALSE);
	    cad.setGenero(usuarioModifico);
	    cad.setSgTipoSolicitudViaje(tipoSolicitudService.find(idTipoSolicitud));
	    cad.setEstatus(estatusService.find(idEstatus));
	    cad.setGerencia(gerenciaService.find(idGerencia));
	    super.create(cad);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en crearCadenaAprobacion " + e.getMessage());
	}
    }

    
    public void eliminarCadenaAprobacion(int idCadenaAprobacion, Usuario usuarioModifico) {
	try {
	    SgCadenaAprobacion cad = find(idCadenaAprobacion);
	    cad.setEliminado(Constantes.BOOLEAN_TRUE);
	    super.create(cad);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en crearCadenaAprobacion " + e.getMessage());
	}

    }

    
    public List<SgCadenaAprobacion> traerCadenaAprobacion() {
	try {
	    return em.createQuery("SELECT c FROM SgCadenaAprobacion c "
		    + " WHERE c.eliminado = :eli ORDER BY c.estatus.id ASC").setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer las cadenas de aprobacion " + e.getMessage());
	    return null;
	}
    }

    
    public SgCadenaAprobacion traerCadenaAprobacionPorEstatusYTipoSolicitud(int idEstatus, int idTipoSolicitud) {
	UtilLog4j.log.info(this, "Estatus a buscar " + idEstatus);
	UtilLog4j.log.info(this, "tipo de solicitud a buscar " + idTipoSolicitud);
	try {
	    return (SgCadenaAprobacion) em.createQuery("SELECT c FROM SgCadenaAprobacion c "
		    + " WHERE c.estatus.id = :idEstatus AND c.sgTipoSolicitudViaje.id = :idTipoSolicitud AND c.eliminado = :eli").setParameter("eli", Constantes.BOOLEAN_FALSE).setParameter("idTipoSolicitud", idTipoSolicitud).setParameter("idEstatus", idEstatus).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer single result en cadenas de aprobacion" + e.getMessage());
	    return null;
	}
    }

    
    public List<SgCadenaAprobacion> traerCadenaAprobacionPorTipoSolicitud(int tipoSolicitud) {
	UtilLog4j.log.info(this, "Idtipo de solicitud a buscr " + tipoSolicitud);
	try {
	    return em.createQuery("SELECT c FROM SgCadenaAprobacion c "
		    + " WHERE c.eliminado = :eli"
		    + " AND c.sgTipoSolicitudViaje.id = :tipoSolicitud"
		    + " ORDER BY c.estatus.id ASC").setParameter("tipoSolicitud", tipoSolicitud).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción al traer las cadenas de aprobación por tipo de solicitud " + e.getMessage());
	    e.printStackTrace();
	    return Collections.EMPTY_LIST;
	}
    }

    
    public List<CadenaAprobacionSolicitudVO> traerCadenaAprobacionPorTipoSolicitudNativa(int tipoSolicitud) {
	UtilLog4j.log.info(this, "Idtipo de solicitud a buscr " + tipoSolicitud);
	String q = "";
	//Controla el campo, por defaul es el 1 (Nejo)
	Integer idCampo = 1;
	try {
	    q = "SELECT cad.ID as idCadena,"//0
		    + " ge.ID as idGerencia,"//1
		    + " ge.NOMBRE as nombreGerencia,"//2
		    + " es.ID as idEstatus,"//3
		    + " es.NOMBRE as nombreEstatus,"//4
		    + " (SELECT us.nombre || ' - ' || rhp.NOMBRE AS puesto         "
		    + "                FROM AP_CAMPO_USUARIO_RH_PUESTO apcup, RH_PUESTO rhp,Usuario us"
		    + "                WHERE apcup.AP_CAMPO = " + idCampo + " AND apcup.USUARIO = (select apcg.RESPONSABLE "
		    + "                                          From AP_CAMPO_GERENCIA apcg"
		    + "                                          where apcg.AP_CAMPO = " + idCampo + " AND apcg.GERENCIA = ge.id AND apcg.ELIMINADO='" + Constantes.BOOLEAN_FALSE + "' )"
		    + "              AND apcup.RH_PUESTO = rhp.ID  "
		    + "              AND apcup.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
		    + "              AND apcup.USUARIO = us.id)"//5 Nombre +  puesto
		    + " FROM SG_CADENA_APROBACION cad,gerencia ge,Estatus es"
		    + " WHERE cad.SG_TIPO_SOLICITUD_VIAJE = " + tipoSolicitud
		    + "      AND cad.GERENCIA = ge.ID "
		    + "      AND cad.ESTATUS = es.ID "
		    + "      order by es.id asc";

	    Query query = em.createNativeQuery(q);
	    List<CadenaAprobacionSolicitudVO> lcad = new ArrayList<CadenaAprobacionSolicitudVO>();
	    List<Object[]> l = query.getResultList();
	    for (Object[] objects : l) {
		CadenaAprobacionSolicitudVO cad = new CadenaAprobacionSolicitudVO();
		cad.setId((Integer) objects[0]);
		cad.setIdGerencia((Integer) objects[1]);
		cad.setNombreGerencia((String) objects[2]);
		cad.setIdEstatus((Integer) objects[3]);
		cad.setNombreEstatus((String) objects[4]);
		String Usuario_Puesto = ((String) objects[5]);
		String[] a1 = Usuario_Puesto.split("-");
		cad.setNombreResponsableGerencia(a1[0]);
		cad.setNombrePuestoResponsableGerencia(a1[1]);
		lcad.add(cad);
	    }
	    return lcad;

//            return em.createQuery("SELECT c FROM SgCadenaAprobacion c "
//                    + " WHERE c.eliminado = :eli"
//                    + " AND c.sgTipoSolicitudViaje.id = :tipoSolicitud"
//                    + " ORDER BY c.estatus.id ASC")
//                    .setParameter("tipoSolicitud",tipoSolicitud )
//                    .setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción al traer las cadenas de aprobación por tipo de solicitud " + e.getMessage());
	    return null;
	}
    }

    
    public SgCadenaAprobacion crearCadenaAprobacion(CadenaAprobacionSolicitudVO cadenaVo, Usuario usuario) {
	try {
	    SgCadenaAprobacion cadena = new SgCadenaAprobacion();
	    cadena.setEstatus(estatusService.find(cadenaVo.getIdEstatus()));
	    cadena.setGerencia(gerenciaService.find(cadenaVo.getIdGerencia()));
	    cadena.setSgTipoSolicitudViaje(tipoSolicitudService.find(cadenaVo.getIdTipoSolicitudViaje()));

	    cadena.setEliminado(Constantes.BOOLEAN_FALSE);
	    cadena.setGenero(usuario);
	    cadena.setFechaGenero(new Date());
	    cadena.setHoraGenero(new Date());
	    super.create(cadena);
	    return cadena;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en crearCadenaAprobacion " + e.getMessage());
	    return null;
	}
    }

    
    public void modificarCadenaAprobacion(SgCadenaAprobacion cadena, Gerencia gerencia, Usuario usuarioModifico) {
	try {
	    cadena.setGerencia(gerencia);
	    cadena.setModifico(usuarioModifico);
	    cadena.setFechaModifico(new Date());
	    cadena.setHoraModifico(new Date());
	    super.edit(cadena);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en modifcar  " + e.getMessage());
	}
    }

    
    public boolean buscarTipoSolicitudViajeOcupado(int idTipoSolicitudViaje) {
	try {
	    if (!traerCadenaAprobacionPorTipoSolicitud(idTipoSolicitudViaje).isEmpty()) {
		return true;
	    } else {
		return false;
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al buscar por tipo de solicitud " + e.getMessage());
	    return false;
	}
    }

    
    public List<CadenaAprobacionSolicitudVO> traerCadenasAprobacion(int idTipoSolicitudViaje) {
	UtilLog4j.log.info(this, "traerCadenasAprobacion" + idTipoSolicitudViaje);
	List<CadenaAprobacionSolicitudVO> lcad = null;
	try {
	    clearQuery();
	    appendQuery(" Select c.id,");
	    appendQuery(" c.SG_TIPO_SOLICITUD_VIAJE,");
	    appendQuery(" case when c.GERENCIA is null then 0 else c.GERENCIA end,");
	    appendQuery(" case when c.ESTATUS is null then 0 else c.ESTATUS end ,");
	    appendQuery(" case when c.SI_ROL is null then 0  else c.SI_ROL end,");
	    appendQuery(" c.APRUEBA_GERENTE_AREA,");
	    appendQuery(" c.VERFICAR_SEMAFORO_ALTERNO, ");
	    appendQuery(" c.APRUEBA_ROL ");

	    appendQuery(" From sg_cadena_aprobacion c");
	    appendQuery(" Where c.SG_TIPO_SOLICITUD_VIAJE = ");
	    appendQuery(idTipoSolicitudViaje);
	    appendQuery(" and c.ELIMINADO = 'False'");
	    appendQuery(" order by c.ESTATUS");

	    List<Object[]> l = em.createNativeQuery(getStringQuery()).getResultList();
	    if (l != null && !l.isEmpty()) {
		lcad = new ArrayList<CadenaAprobacionSolicitudVO>();
		for (Object[] objects : l) {
		    lcad.add(castCadenaAprobacion(objects));
		}
		//traer la ultima cadena de aprobacion
		lcad.get(lcad.size() - 1).setUltimaCadena(true);
		UtilLog4j.log.info(this, "la ultima cadena de aprobacion es  " + lcad.get(lcad.size() - 1).getIdEstatus());
	    }
	    return lcad;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Expcepcion al traer " + e.getMessage());
	    return null;
	}
    }

    private CadenaAprobacionSolicitudVO castCadenaAprobacion(Object[] objects) {
	try {
	    CadenaAprobacionSolicitudVO cad = new CadenaAprobacionSolicitudVO();
	    cad.setId((Integer) objects[0]);
	    cad.setIdTipoSolicitudViaje((Integer) objects[1]);
	    cad.setIdGerencia((Integer) objects[2]);
	    cad.setIdEstatus((Integer) objects[3]);

	    cad.setIdSiRol((Integer) objects[4]);
	    cad.setApruebaGerenteArea((Boolean) objects[5]);
	    cad.setVerificarSemaforoAlterno((Boolean) objects[6]);
	    cad.setApruebaRol((Boolean) objects[7]);
	    return cad;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al castear cadena de aproibacionb " + e.getMessage());
	    return null;
	}
    }
}
