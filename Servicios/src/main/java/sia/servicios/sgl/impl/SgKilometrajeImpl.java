/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgKilometraje;
import sia.modelo.SgOficina;
import sia.modelo.SgVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sgl.vehiculo.vo.SgKilometrajeVo;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgKilometrajeImpl extends AbstractFacade<SgKilometraje>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //
    @Inject
    private SgTipoImpl tipoService;
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    private SgVehiculoImpl vehiculoRemote;    
    @Inject
    private SgVehiculoMantenimientoImpl sgVehiculoMantenimientoRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgKilometrajeImpl() {
	super(SgKilometraje.class);
    }

    
    public SgKilometraje createKilometrajeActual(int idVehiculo, int idTipoEspecifico, int kilomentrajeActual, Usuario usuarioGenero) {
	UtilLog4j.log.info(this, "createKilometrajeActual");
	UtilLog4j.log.info(this, "kilometraje " + kilomentrajeActual);
	UtilLog4j.log.info(this, "tipo " + idTipoEspecifico);	
	try {
	    SgKilometraje kilometrajeNuevo = new SgKilometraje();
	    SgKilometraje kilometrajePasado = findKilometrajeActualVehiculo(idVehiculo);
	    kilometrajeNuevo.setSgVehiculo(vehiculoRemote.find(idVehiculo));
	    kilometrajeNuevo.setSgTipo(this.tipoService.find(14));
	    kilometrajeNuevo.setSgTipoEspecifico(this.tipoEspecificoService.find(idTipoEspecifico));
	    kilometrajeNuevo.setActual(Constantes.BOOLEAN_TRUE);
	    kilometrajeNuevo.setKilometraje(kilomentrajeActual);
	    kilometrajeNuevo.setEliminado(Constantes.BOOLEAN_FALSE);
	    kilometrajeNuevo.setGenero(usuarioGenero);
	    kilometrajeNuevo.setFechaGenero(new Date());
	    kilometrajeNuevo.setHoraGenero(new Date());
	    create(kilometrajeNuevo);
	    //poner el kilometraje pasado a falso
	    if (kilometrajePasado != null) {		
		kilometrajePasado.setActual(Constantes.BOOLEAN_FALSE);
		edit(kilometrajePasado);
	    }
	    return kilometrajeNuevo;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en crear kilometraje " + e.getMessage());
	    return null;
	}
    }

    
    public void eliminarKilometraje(SgKilometraje km, Usuario usuarioElimino) {	
	try {	    
	    km.setEliminado(Constantes.BOOLEAN_TRUE);
	    super.edit(km);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en eliminar kilometraje " + e.getMessage());
	}
    }

    
    public SgKilometraje traerUltimoKm(int idVehiculo) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.traerUltimoKm");
	List<SgKilometraje> lret = null;
	try {
	    lret = em.createQuery("SELECT k FROM SgKilometraje k "
		    + " WHERE k.sgVehiculo.id = :idVehiculo AND k.eliminado = :eliminado ORDER BY k.id DESC ").setParameter("idVehiculo", idVehiculo).setParameter("eliminado", Constantes.BOOLEAN_FALSE).setMaxResults(1).getResultList();
	    if (!lret.isEmpty()) {
		UtilLog4j.log.info(this, "todo bien en traer km");
		return lret.get(0);
	    } else {
		UtilLog4j.log.info(this, "todo bien en traer km,  no existen km");
		return null;
	    }
	} catch (Exception e) {

	    UtilLog4j.log.fatal(this, "Excpcion en traer le ultimo");
	    return null;
	}
    }

    
    public void activarKilometraje(SgKilometraje km, Usuario usuarioModifico) {	
	try {
	    UtilLog4j.log.info(this, "activarKilometraje en impl");	    
	    UtilLog4j.log.info(this, "paso tostring()");
	    km.setActual(Constantes.BOOLEAN_TRUE);
	    UtilLog4j.log.info(this, "activo");
	    edit(km);
	    UtilLog4j.log.info(this, "edito");
	    UtilLog4j.log.info(this, "todo bien");
	} catch (Exception e) {

	    UtilLog4j.log.fatal(this, "Excepcion en activacion de km" + e.getMessage());
	}
    }

    
    public void updateAllKilometrajes(List<SgKilometrajeVo> kilometrajes, String idGenero) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.updateAllKilometrajes");

	for (SgKilometrajeVo vo : kilometrajes) {
	    createKilometrajeActual(vo.getIdSgVehiculo(), 11, vo.getKilometrajeNuevo(), new Usuario(idGenero));
	}
    }

    
    public SgKilometraje restartKilometraje(int vehiculo, int oficina, String motivoReinicio, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.restartKilometraje()");

	try {
	    SgKilometraje kilometraje = createKilometrajeActual(vehiculo, 7, 0, new Usuario(idUsuario));
	    kilometraje.setObservacion("Reinicio - " + motivoReinicio);

	    editKilometrajeActual(kilometraje, kilometraje.getSgTipoEspecifico().getId(), kilometraje.getKilometraje(), new Usuario(idUsuario));

	    //desactivar el mantenimiento preventivo activo
	    sgVehiculoMantenimientoRemote.desactivarMantenimientoPreventivoActual(vehiculo, idUsuario);

	    //enviar correo de aviso para que cree un mantenimiento preventivo con el motivo de reinicio
	    this.notificacionServiciosGeneralesRemote.enviarNofiticacionReinicioModificacionKilometraje(vehiculo, oficina, 0, 0, motivoReinicio, idUsuario, false);

	    return kilometraje;

	} catch (Exception ex) {

	    UtilLog4j.log.fatal(this, "Excepcion al restart kilometreaje " + ex.getMessage());
	    return null;
	}
    }

    
    public void updateSgKilometrajeActual(int idSgVehiculo, int oficina, int kilometraje, String motivo, String idUsuario) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.updateSgKilometrajeActual()");
	SgKilometraje sk = findKilometrajeActualVehiculo(idSgVehiculo);
	if (this.notificacionServiciosGeneralesRemote.enviarNofiticacionReinicioModificacionKilometraje(idSgVehiculo, oficina, sk.getKilometraje(), kilometraje, motivo, idUsuario, true)) {

	    sk.setModifico(new Usuario(idUsuario));
	    sk.setHoraModifico(new Date());
	    sk.setKilometraje(kilometraje);
	    sk.setObservacion("Modificación - " + motivo);

	    edit(sk);
	}
	//enviar al log
    }

    
    public List<SgKilometraje> findLastKilometrajes(SgVehiculo vehiculo, int maximoResultados) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.findLastKilometrajes");
	List<SgKilometraje> lret = null;
	try {
	    return em.createQuery("SELECT k FROM SgKilometraje k "
		    + " WHERE k.sgVehiculo.id = :idVehiculo AND k.eliminado = :eliminado ORDER BY k.id DESC ").setParameter("idVehiculo", vehiculo.getId()).setParameter("eliminado", Constantes.BOOLEAN_FALSE).setMaxResults(maximoResultados).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer el kilometraje actual " + e.getMessage());
	    return null;
	}
    }

    
    public SgKilometraje findKilometrajeActualVehiculo(int idVehiculo) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.findKilometrajeActual");
	UtilLog4j.log.info(this, "id vehiculo: " + idVehiculo);
	List<SgKilometraje> ret = null;
	try {
	    ret = em.createQuery("SELECT k FROM SgKilometraje k "
		    + " WHERE k.sgVehiculo.id = :idVehiculo AND k.actual = :actual AND k.eliminado = :eliminado ").setParameter("idVehiculo", idVehiculo).setParameter("actual", Constantes.BOOLEAN_TRUE).setParameter("eliminado", Constantes.BOOLEAN_FALSE).
		    getResultList();
	    UtilLog4j.log.info(this, "size km" + ret.size());
	    if (ret.size() > 0) {
		UtilLog4j.log.info(this, "trae kilometraje");
		return ret.get(0);
	    } else {
		UtilLog4j.log.info(this, "no trae kilometraje");
		return null;
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción al traer el kilometraje actual " + e.getMessage());
	    return null;
	}
    }

    /*
     * Modificar un kilometraje
     */
    
    public SgKilometraje editKilometrajeActual(SgKilometraje kilometraje, int idTipoEspecifico, int kilomentrajeActual, Usuario usuarioModifico) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.editKilometrajeActual");	
	try {	    
	    kilometraje.setActual(Constantes.BOOLEAN_TRUE);
	    kilometraje.setModifico(usuarioModifico);
	    kilometraje.setFechaModifico(new Date());
	    kilometraje.setHoraModifico(new Date());
	    kilometraje.setKilometraje(kilomentrajeActual);
	    kilometraje.setSgTipo(this.tipoService.find(14));
	    kilometraje.setSgTipoEspecifico(this.tipoEspecificoService.find(idTipoEspecifico));
	    super.edit(kilometraje);
	    return kilometraje;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al editar  el kilometraje actual " + e.getMessage());
	    return null;
	}
    }
    /*
     * @Descripcion: Regresa a estado actual=true al registro anterior al actual
     * ultimo
     *
     *
     */

    
    public boolean regresarKilometrajeActualAnterior(SgVehiculo sgVehiculo, SgKilometraje sgKilometrajeActual, Usuario usuarioGenero) {
	UtilLog4j.log.info(this, "Entrando a regreso de kilometraje ");	
	List<SgKilometraje> listaKm = null;

	SgKilometraje kilometrajeAnterior = null;
	SgKilometraje kilometrajeActual = null;
	try {
	    listaKm = findLastKilometrajes(sgVehiculo, 2);
	    if (!listaKm.isEmpty()) {
		if (listaKm.size() > 0) {
		    UtilLog4j.log.info(this, "Fue mayor a 0 " + listaKm.size());
		    kilometrajeAnterior = listaKm.get(1);
		    UtilLog4j.log.info(this, "kilometraje anterior " + kilometrajeAnterior.getKilometraje());
		    UtilLog4j.log.info(this, "Actual = " + kilometrajeAnterior.isActual());
		}
	    }

	    if (kilometrajeAnterior != null) {
		UtilLog4j.log.info(this, "si hay kilometraje anterior ");
		kilometrajeAnterior.setActual(Constantes.BOOLEAN_TRUE);
		kilometrajeAnterior.setModifico(usuarioGenero);
		kilometrajeAnterior.setFechaModifico(new Date());
		kilometrajeAnterior.setHoraModifico(new Date());

		UtilLog4j.log.info(this, "buscar kilometraje actual para modificarlo a eliminado ");
		// kilometrajeActual = sgKilometrajeActual; //findKilometrajeActualVehiculo(sgVehiculo.getId());
		sgKilometrajeActual.setEliminado(Constantes.BOOLEAN_TRUE);
		sgKilometrajeActual.setModifico(usuarioGenero);
		sgKilometrajeActual.setFechaModifico(new Date());
		sgKilometrajeActual.setHoraModifico(new Date());

		super.edit(sgKilometrajeActual);
		//logService.create(SgKilometraje.class.getName(), kilometrajeActual.getId(), eventoService.find(2), usuarioGenero.getId(), antesEventoActual, kilometrajeActual.toString());
		super.edit(kilometrajeAnterior);
		//logService.create(SgKilometraje.class.getName(), kilometrajeAnterior.getId(), eventoService.find(2), usuarioGenero.getId(), antesEventoAnterior, kilometrajeAnterior.toString());
		UtilLog4j.log.info(this, "modifico bien los registros de kilometrajes");
//                return true;
	    } else {
		UtilLog4j.log.info(this, "No hay kilometraje anterior, buscar km actual para eliinarlo");
		//kilometrajeActual = findKilometrajeActualVehiculo(sgVehiculo.getId());
		UtilLog4j.log.info(this, "Kilometraje actual " + kilometrajeActual.isActual());
		sgKilometrajeActual.setEliminado(Constantes.BOOLEAN_TRUE);
		sgKilometrajeActual.setModifico(usuarioGenero);
		sgKilometrajeActual.setFechaModifico(new Date());
		sgKilometrajeActual.setHoraModifico(new Date());
		edit(sgKilometrajeActual);
	    }
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion (SgKilometrajeImpl) al regresar kilometraje actual " + e.getMessage());
	    return false;
	}
    }

    
    public List<SgKilometraje> traerKilometrajesActivos(SgOficina oficina, int kmMenor) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.traerKilometrajesActivos");
	List<SgKilometraje> ret = null;
	try {
	    ret = em.createQuery("SELECT k FROM SgKilometraje k "
		    + " WHERE k.actual = :TRUE  "
		    + " AND k.eliminado = :FALSE"
		    + " AND k.sgVehiculo.id "
		    + " IN "
		    + " (SELECT m.sgVehiculo.id "
		    + "  FROM SgVehiculoMantenimiento m"
		    + "  WHERE m.terminado = :TRUE"
		    + "   AND (m.proxMantenimientoKilometraje - :kmMenor ) <= k.kilometraje"
		    + "   AND m.sgVehiculo.id "
		    + "   IN "
		    + "      (SELECT v.id "
		    + "       FROM SgVehiculo v "
		    + "       WHERE v.sgOficina = :oficina AND v.eliminado = :FALSE)) "
		    + " ORDER BY k.sgVehiculo.id").setParameter("TRUE", Constantes.BOOLEAN_TRUE).setParameter("FALSE", Constantes.BOOLEAN_FALSE).setParameter("oficina", oficina).setParameter("kmMenor", kmMenor).getResultList();
	    UtilLog4j.log.info(this, "kilometrajes activos ok..");
	    return ret;

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer el kilometraje actual " + e.getMessage());
	    return null;
	}

    }

    
    public List<SgKilometraje> traerKilometrajeProximaFechaPorRealizar(SgOficina oficina, Date proximaFecha) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.traerKilometrajeProximaFechaPorRealizar");
	UtilLog4j.log.info(this, "oficina " + oficina.getNombre());
	UtilLog4j.log.info(this, "prox fecha " + proximaFecha);
	List<SgKilometraje> ret = null;
	try {
	    ret = em.createQuery("SELECT k FROM SgKilometraje k "
		    + " WHERE k.actual = :TRUE  "
		    + " AND k.eliminado = :FALSE"
		    + " AND k.sgVehiculo.id "
		    + " IN "
		    + " (SELECT m.sgVehiculo.id "
		    + "  FROM SgVehiculoMantenimiento m"
		    + "  WHERE m.terminado = :TRUE"
		    + "   AND m.proxMantenimientoFecha <= :proxFecha "
		    + "   AND m.sgVehiculo.id "
		    + "   IN "
		    + "      (SELECT v.id "
		    + "       FROM SgVehiculo v "
		    + "       WHERE v.sgOficina = :oficina AND v.eliminado = :FALSE)) "
		    + " ORDER BY k.sgVehiculo.id").setParameter("TRUE", Constantes.BOOLEAN_TRUE).setParameter("FALSE", Constantes.BOOLEAN_FALSE).setParameter("oficina", oficina).setParameter("proxFecha", proximaFecha).getResultList();
	    return ret;

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer el kilometraje actual " + e.getMessage());
	    return null;
	}
    }

    
    public List<SgKilometrajeVo> getAllSgKilometrajeBySgVehiculoSgOficina(int idSgOficina, String orderByField, boolean sortAscending) {
	UtilLog4j.log.info(this, "SgKilometrajeImpl.getAllSgKilometrajeBySgVehiculoSgOficina()");

	clearQuery();
	query.append("SELECT DISTINCT k.ID,  v.ID AS ID_SG_VEHICULO,  o.ID AS ID_SG_OFICINA_SG_VEHICULO,  ma.NOMBRE AS NOMBRE_SG_MARGA_SG_VEHICULO,  ");
	query.append("  mo.NOMBRE AS NOMBRE_SG_MODELO_SG_VEHICULO, v.SERIE AS SERIE_SG_VEHICULO, v.NUMERO_PLACA AS NUMERO_PLACA_SG_VEHICULO,  ");
	query.append("  k.KILOMETRAJE,  k.SG_TIPO,  k.SG_TIPO_ESPECIFICO,  k.OBSERVACION FROM SG_KILOMETRAJE k");
	query.append("      inner join SG_VEHICULO v on k.SG_VEHICULO = v.ID");
	query.append("      inner join SG_MARCA ma on v.SG_MARCA = ma.ID");
	query.append("      inner join SG_MODELO mo on v.SG_MODELO = mo.ID");
	query.append("      inner join SG_OFICINA o on v.SG_OFICINA = o.ID");
	query.append("      inner join SG_TIPO t on k.SG_TIPO = t.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on k.SG_TIPO_ESPECIFICO = te.ID");
	query.append("  WHERE k.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  AND k.ACTUAL='").append(Constantes.BOOLEAN_TRUE).append("'");
	query.append("  AND v.SG_OFICINA =  ").append(idSgOficina);
	query.append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  ORDER BY mo.nombre asc");
//

	UtilLog4j.log.info(this, "query: " + query.toString());

	List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();
	List<SgKilometrajeVo> list = new ArrayList<SgKilometrajeVo>();

	SgKilometrajeVo vo = null;

	for (Object[] objects : result) {
	    vo = new SgKilometrajeVo();
	    vo.setId((Integer) objects[0]);
	    vo.setIdSgVehiculo((Integer) objects[1]);
	    vo.setIdSgOficina((Integer) objects[2]);
	    vo.setNombreMarcaVehiculo((String) objects[3]);
	    vo.setNombreModeloVehiculo((String) objects[4]);
	    vo.setSerieVehiculo((String) objects[5]);
	    vo.setPlacaVehiculo((String) objects[6]);
	    vo.setKilometrajeActual((Integer) objects[7]);
	    vo.setIdSgTipo((Integer) objects[8]);
	    vo.setIdSgTipoEspecifico((Integer) objects[9]);
	    vo.setObservacion((String) objects[10]);
	    list.add(vo);
	}

	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgKilometrajeVo");

	return (list != null ? list : Collections.EMPTY_LIST);
    }

    
    public List<SgKilometrajeVo> traerKilometrajeActualYAnterior(int idVehiculo) {
	try {
	    UtilLog4j.log.info(this, "SgKilometrajeImpl.getAllSgKilometrajeBySgVehiculoSgOficina()");

	    clearQuery();
	    appendQuery(" Select ");
	    appendQuery(" k.id,");//0
	    appendQuery(" k.ACTUAL,");//1
	    appendQuery(" k.KILOMETRAJE,");//2
	    appendQuery(" k.SG_TIPO_ESPECIFICO,");//3
	    appendQuery(" k.SG_TIPO,");//4
	    appendQuery(" es.NOMBRE");//5
	    appendQuery(" from SG_KILOMETRAJE k,SG_TIPO_ESPECIFICO es");
	    appendQuery(" where k.SG_VEHICULO = ");
	    appendQuery(idVehiculo);
	    appendQuery(" and k.ELIMINADO = 'False' ");
	    appendQuery(" and k.SG_TIPO_ESPECIFICO = es.id");
	    appendQuery(" order by k.id desc limit 2");

	    List<Object[]> l = em.createNativeQuery(getStringQuery()).getResultList();

	    List<SgKilometrajeVo> list = new ArrayList<SgKilometrajeVo>();

	    SgKilometrajeVo vo = null;

	    for (Object[] objects : l) {
		vo = new SgKilometrajeVo();
		vo.setId((Integer) objects[0]);
		vo.setActual(String.valueOf(objects[1]).equals(Constantes.BOOLEAN_TRUE) ? true : false);
		vo.setKilometrajeActual((Integer) objects[2]);
		vo.setIdSgTipoEspecifico((Integer) objects[3]);
		vo.setIdSgTipo((Integer) objects[4]);
		vo.setNombreTipoEspecifico((String) objects[5]);
		list.add(vo);
	    }
	    UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgKilometrajeVo");
	    return (list != null ? list : Collections.EMPTY_LIST);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en traerKilometrajeActualYAnterior " + e.getMessage());
	    return null;
	}
    }

    
    public List<TipoEspecificoVo> traerConceptosPago(int tipo) {
	clearQuery();
	List<TipoEspecificoVo> lt = null;
	query.append("select distinct(te.ID), te.NOMBRE from SG_VEHICULO_MANTENIMIENTO vm");
	query.append("      inner join SG_KILOMETRAJE k on vm.SG_KILOMETRAJE = k.id");
	query.append("      inner join SG_TIPO t on k.SG_TIPO = t.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on k.SG_TIPO_ESPECIFICO = te.ID");
	query.append("  where k.SG_TIPO = ").append(tipo);
	try {
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		lt = new ArrayList<TipoEspecificoVo>();
		for (Object[] objects : lo) {
		    lt.add(castConceptos(objects));
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrió un error al traer conceptos de gastos " + e.getMessage());
	}
	return lt;
    }

    private TipoEspecificoVo castConceptos(Object[] objects) {
	TipoEspecificoVo tipoEspecificoVo = new TipoEspecificoVo();
	tipoEspecificoVo.setId((Integer) objects[0]);
	tipoEspecificoVo.setNombre((String) objects[1]);
	return tipoEspecificoVo;
    }
}
