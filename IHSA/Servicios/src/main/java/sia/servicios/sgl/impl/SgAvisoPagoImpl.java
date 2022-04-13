/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.*;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgAvisoPagoImpl extends AbstractFacade<SgAvisoPago>{

    @Inject
    SgPeriodicidadImpl periodicidadService;
    @Inject
    SgStaffImpl staffService;
    @Inject
    SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    SgAvisoPagoStaffImpl relacionAvisoPagoStaffService;
    @Inject
    SgAvisoPagoOficinaImpl relacionAvisoPagoOficinaService;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgAvisoPagoImpl() {
	super(SgAvisoPago.class);
    }

    
    public SgAvisoPago findSgAvisoPagoRepetido(int idTipoEspecifico) {
	SgAvisoPago pago = null;
	try {
	    UtilLog4j.log.info(this, "Buscar el objeto de aviso en tabla sgAvisoPago");
	    //busco primero en la tabla de platillas..
	    pago = (SgAvisoPago) em.createQuery("SELECT p FROM SgAvisoPago p WHERE "
		    + " p.sgTipoEspecifico.id = :idTipoEspecifico AND"
		    + " p.eliminado = :eliminado").setParameter("idTipoEspecifico", idTipoEspecifico).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList().get(0);
	    UtilLog4j.log.info(this, "resultado " + pago.getSgTipoEspecifico().getNombre());
	    UtilLog4j.log.info(this, "Todo bien en la busqueda de repetidos");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion en la consulta de pagos repetidos" + e.getMessage());
	    return null;
	}
	return pago;
    }

    
    public SgAvisoPago findSgAvisoPagoRepetidoAtributos(int idTipoEspecifico, int idPeriodicidad, int diaEstimadoPago, int diaAnticipado) {
	SgAvisoPago pago = null;
	try {
	    UtilLog4j.log.info(this, "Buscar el objeto de aviso en tabla sgAvisoPago, comprarando sus atributos");
	    pago = (SgAvisoPago) em.createQuery("SELECT p FROM SgAvisoPago p WHERE "
		    + " p.sgPeriodicidad.id = :idPeriodicidad AND"
		    + " p.sgPeriodicidad.id = :idPeriodicidad AND"
		    + " p.sgTipoEspecifico.id = :idTipoEspecifico AND"
		    + " p.diaEstimadoPago = :diaEstimadoPago AND "
		    + " p.diaAnticipadoPago = :diaAnticipadoPago AND"
		    + " p.eliminado = :eliminado").setParameter("idPeriodicidad", idPeriodicidad).setParameter("idTipoEspecifico", idTipoEspecifico).setParameter("diaEstimadoPago", diaEstimadoPago).setParameter("diaAnticipadoPago", diaAnticipado).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList().get(0);
	    UtilLog4j.log.info(this, "resultado " + pago.getSgTipoEspecifico().getNombre());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion en la consulta de pagos repetidos" + e.getMessage());
	    return null;
	}
	return pago;
    }

    
    public SgAvisoPago createAvisoPago(SgAvisoPago avisoPago, int idStaff, int idPeriodicidad, int idTipoEspecifico, Usuario usuarioGenero) {
	UtilLog4j.log.info(this, "sgAvisoPagoImpl.createAvisoPago");
	try {
	    UtilLog4j.log.info(this, "Aviso " + avisoPago.getDiaEstimadoPago());
	    UtilLog4j.log.info(this, "fecha  " + avisoPago.getFechaProximoAviso());
	    UtilLog4j.log.info(this, "idperio " + idPeriodicidad);

	    SgTipoEspecifico tipoEspecifico = tipoEspecificoService.find(idTipoEspecifico);
	    avisoPago.setSgTipoEspecifico(tipoEspecifico);
	    avisoPago.setSgPeriodicidad(periodicidadService.find(idPeriodicidad));
	    avisoPago.setEliminado(Constantes.BOOLEAN_FALSE);
	    avisoPago.setFechaGenero(new Date());
	    avisoPago.setHoraGenero(new Date());
	    avisoPago.setGenero(usuarioGenero);
	    super.create(avisoPago);
	    UtilLog4j.log.info(this, "Se creo un nuevo registro de SgAviso");

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion --" + e.getMessage());
	}
	UtilLog4j.log.info(this, "antes de retornar" + avisoPago.getId());
	return avisoPago;
    }

    
    public void editAvisoPago(SgAvisoPago avisoPago, int idPeriodicidad, int idTipoEspecifico, Usuario usuarioGenero) {
	UtilLog4j.log.info(this, "sgAvisoPagoImpl.editAvisoPagoStaff");
	try {
	    //avisoPago.setSgTipoEspecifico(tipoEspecificoService.find(idTipoEspecifico));
	    avisoPago.setSgPeriodicidad(periodicidadService.find(idPeriodicidad));
	    avisoPago.setEliminado(Constantes.BOOLEAN_FALSE);
	    avisoPago.setFechaGenero(new Date());
	    avisoPago.setHoraGenero(new Date());
	    avisoPago.setGenero(usuarioGenero);
	    super.edit(avisoPago);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion --" + e.getMessage());
	}
	UtilLog4j.log.info(this, "Se modifico satisfactoriamente el registro de SgAvisoPago");
    }

    
    public List<SgAvisoPago> traerAvisosPagosPorStaffConFechaHoy(SgStaff staff) {
	List<SgAvisoPago> lret = Collections.EMPTY_LIST;
	try {
	    lret = em.createQuery("SELECT r.sgAvisoPago FROM SgAvisoPagoStaff r WHERE "
		    + " r.sgStaff.id = :idStaff AND r.sgAvisoPago.fechaProximoAviso = :fechaHoy AND r.eliminado = :eliminado ORDER BY r.sgAvisoPago.id ASC").setParameter("idStaff", staff.getId()).setParameter("fechaHoy", new Date()).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion en la consulta de fechas de avisos para staff house " + e.getMessage());
	    return Collections.EMPTY_LIST;
	}
	return lret;
    }

    
    public List<SgAvisoPago> traerAvisosPagosPorOficinaConFechaHoy(SgOficina oficina) {
	List<SgAvisoPago> lret = Collections.EMPTY_LIST;
	try {
	    lret = em.createQuery("SELECT r.sgAvisoPago FROM SgAvisoPagoOficina r WHERE "
		    + " r.sgOficina.id = :idOficina AND r.sgAvisoPago.fechaProximoAviso = :fechaHoy AND r.eliminado = :eliminado ORDER BY r.sgAvisoPago.id ASC").setParameter("idOficina", oficina.getId()).setParameter("fechaHoy", new Date()).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion en la consulta de fechas de avisos para oficinas" + e.getMessage());
	    return null;
	}

	return lret;
    }

    
    public void editAvisoPagoNuevaFechaAviso(SgAvisoPago avisoPago, Date fechaNueva) {
	avisoPago.setFechaProximoAviso(fechaNueva);
	super.edit(avisoPago);

    }

    
    public void deletePorAvisoAndOficina(SgAvisoPago sgAvisoPago, int idOficina, Usuario usuarioGenero) {
	try {
	    //SgAvisoPagoOficina r = findSgAvisoPagoRepetidoRelacion(idOficina, sgAvisoPago.getId());
	    if (relacionAvisoPagoOficinaService.findCoutSgAvisoPagoRepetidoRelacion(sgAvisoPago.getId()) == 1) {  //si fue igual a 1 eliminar todo
		sgAvisoPago.setGenero(usuarioGenero);
		sgAvisoPago.setFechaGenero(new Date());
		sgAvisoPago.setHoraGenero(new Date());
		sgAvisoPago.setEliminado(Constantes.BOOLEAN_TRUE);
		super.edit(sgAvisoPago);
		relacionAvisoPagoOficinaService.deleteRelacionAvisoOficina(sgAvisoPago.getId(), idOficina, usuarioGenero);
	    } else {
		//solo eliminar la relacion
		relacionAvisoPagoOficinaService.deleteRelacionAvisoOficina(sgAvisoPago.getId(), idOficina, usuarioGenero);
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion :" + e.getMessage());
	}
    }

    
    public SgAvisoPago findSgAvisoPagoRepetidoAtributosEliminado(int idTipoEspecifico, int idPeriodicidad, int diaEstimadoPago, int diaAnticipado) {
	SgAvisoPago pago = null;
	try {
	    UtilLog4j.log.info(this, "Buscar el aviso en los elementos que estan eliminados,Retorna el objeto si lo encontro..");
	    pago = (SgAvisoPago) em.createQuery("SELECT p FROM SgAvisoPago p WHERE "
		    + " p.sgPeriodicidad.id = :idPeriodicidad AND"
		    + " p.sgPeriodicidad.id = :idPeriodicidad AND"
		    + " p.sgTipoEspecifico.id = :idTipoEspecifico AND"
		    + " p.diaEstimadoPago = :diaEstimadoPago AND "
		    + " p.diaAnticipadoPago = :diaAnticipadoPago AND"
		    + " p.eliminado = :eliminado").setParameter("idPeriodicidad", idPeriodicidad).setParameter("idTipoEspecifico", idTipoEspecifico).setParameter("diaEstimadoPago", diaEstimadoPago).setParameter("diaAnticipadoPago", diaAnticipado).setParameter("eliminado", Constantes.BOOLEAN_TRUE).getResultList().get(0);
	    UtilLog4j.log.info(this, "resultado " + pago.getSgTipoEspecifico().getNombre());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion en la consulta de pagos repetidos" + e.getMessage());
	    return null;
	}
	return pago;
    }

    
    public SgAvisoPago createSgAvisoPagoRepetidoAtributosOficina(int idOficina, int idTipoEspecifico, int idPeriodicidad, int diaEstimadoPago, int diaAnticipado, Usuario usuarioGenero) {
	SgAvisoPago pagoEncontrado = this.findSgAvisoPagoRepetidoAtributosEliminado(idTipoEspecifico, idPeriodicidad, diaEstimadoPago, diaAnticipado);
	if (pagoEncontrado != null) {
	    //activar pago encontrado
	    if (this.activarPago(pagoEncontrado, usuarioGenero)) {
		//verificar la relacion para saber si la relacion eliminada pertenece a la oficina seleccionada,
		SgAvisoPagoOficina relacion = relacionAvisoPagoOficinaService.findSgAvisoPagoRepetidoRelacionParaOficina(idOficina, pagoEncontrado.getId(), Constantes.BOOLEAN_TRUE);
		if (relacion != null) {
		    //encontre la relacion, entonces la debo de activar
		    relacionAvisoPagoOficinaService.activarRelacion(relacion, usuarioGenero);
		} else {
		    //no era su relacion, entonces crear una nueva
		    relacionAvisoPagoOficinaService.createRelacionAvisoPagoOficina(pagoEncontrado, idOficina, usuarioGenero);
		}

	    }

	}

	return pagoEncontrado;
    }

    
    public boolean activarPago(SgAvisoPago sgAvisoPago, Usuario usuarioGenero) {
	UtilLog4j.log.info(this, "sgAvisoPagoImpl.activarPago");
	try {
	    sgAvisoPago.setEliminado(Constantes.BOOLEAN_FALSE);
	    sgAvisoPago.setFechaGenero(new Date());
	    sgAvisoPago.setGenero(usuarioGenero);
	    sgAvisoPago.setHoraGenero(new Date());
	    super.edit(sgAvisoPago);
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Existio un error en la activacion del pago");
	    return false;
	}
    }

    
    public void createAvisoAndRelacionOficina(SgAvisoPago avisoPago, int idOficina, int idPeriodicidad, int idTipoEspecifico, Usuario usuarioGenero) {
	try {
	    relacionAvisoPagoOficinaService.createRelacionAvisoPagoOficina(createAvisoPago(avisoPago, 0, idPeriodicidad, idTipoEspecifico, usuarioGenero), idOficina, usuarioGenero);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion " + e.getMessage());

	}
    }

    
    public SgAvisoPagoOficina findSgAvisoPagoRepetidoRelacion(int idOficina, int idAvisoPago) {
	UtilLog4j.log.info(this, "Buscar el objeto Aviso en la relacion");
	SgAvisoPagoOficina relacion;
	try {
	    relacion = (SgAvisoPagoOficina) em.createQuery("SELECT r FROM SgAvisoPagoOficina r "
		    + " WHERE r.sgOficina.id = :idoficina AND "
		    + " r.sgAvisoPago.id = :idAvisoPago AND "
		    + " r.eliminado = :eliminado ")
		    .setParameter("idOficina", idOficina)
		    .setParameter("idAvisoPago", idAvisoPago)
		    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
		    .getSingleResult();

	    UtilLog4j.log.info(this, "Todo bien en la busqueda de relacion de oficina");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion en la consulta " + e.getMessage());
	    return null;
	}
	if (relacion != null) {
//            UtilLog4j.log.info(this,"Objeto a retornar " + re.getSgTipoEspecifico().getNombre());
	} else {
	    UtilLog4j.log.info(this, "El objeto buscado es null");
	}
	return relacion;
    }

    
    public void deleteAviso(SgAvisoPago aviso, Usuario usuarioGenero) {
	UtilLog4j.log.info(this, "sgAvisoPagoImpl.deleteAviso");
	try {
	    //modificado por el nuevo control del log
	    aviso.setEliminado(Constantes.BOOLEAN_TRUE);
	    aviso.setFechaGenero(new Date());
	    aviso.setGenero(usuarioGenero);
	    aviso.setHoraGenero(new Date());
	    super.edit(aviso);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Existió un error en la activacion del pago");
	}
    }

    
    public void ponerComoTipoEspecificoUsado(SgAvisoPago avisoPago, Usuario usuario) {
	UtilLog4j.log.info(this, "poner como tipo especifico usado");
	try {
	    tipoEspecificoService.ponerUsadoTipoEspecifico(avisoPago.getSgTipoEspecifico().getId(), usuario);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "excepcion " + e.getMessage());
	}
    }
}
