/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.math.BigDecimal;
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
import sia.modelo.SgGastoViaje;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.SgGastoViajeVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgGastoViajeImpl extends AbstractFacade<SgGastoViaje>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private MonedaImpl monedaRemote;
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SgTipoImpl sgTipoRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;    

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgGastoViajeImpl() {
	super(SgGastoViaje.class);
    }

    
    public void save(BigDecimal importe, String observacion, int idMoneda, int idSgViaje, int idSgTipo, int idSgTipoEspecifico, String idUsuario) {
	UtilLog4j.log.info(this, "SgGastoViajeImpl.save()");

	SgGastoViaje sgGastoViaje = new SgGastoViaje();
	SgTipoEspecifico sgTipoEspecifico = this.sgTipoEspecificoRemote.find(idSgTipoEspecifico);
	Usuario usuario = new Usuario(idUsuario);

	sgGastoViaje.setImporte(importe);
	sgGastoViaje.setObservacion(observacion);
	sgGastoViaje.setMoneda(this.monedaRemote.find(idMoneda));
	sgGastoViaje.setSgViaje(this.sgViajeRemote.find(idSgViaje));
	sgGastoViaje.setSgTipo(this.sgTipoRemote.find(idSgTipo));
	sgGastoViaje.setSgTipoEspecifico(sgTipoEspecifico);
	sgGastoViaje.setGenero(usuario);
	sgGastoViaje.setFechaGenero(new Date());
	sgGastoViaje.setHoraGenero(new Date());
	sgGastoViaje.setEliminado(Constantes.NO_ELIMINADO);

	//Poner Usado el Tipo Específico
	this.sgTipoEspecificoRemote.ponerUsadoTipoEspecifico(idSgTipoEspecifico, usuario);

	create(sgGastoViaje);
	UtilLog4j.log.info(this, "SgGastoViaje CREATED SUCCESSFULLY");
    }

    
    public void update(int idSgGastoViaje, BigDecimal importe, String observacion, int idSgTipoEspecifico, int idMoneda, String idUsuario) {
	UtilLog4j.log.info(this, "SgGastoViajeImpl.update()");

	SgGastoViaje sgGastoViaje = find(idSgGastoViaje);
	sgGastoViaje.setImporte(importe);
	sgGastoViaje.setObservacion(observacion);
	sgGastoViaje.setMoneda(this.monedaRemote.find(idMoneda));
	sgGastoViaje.setSgTipoEspecifico(this.sgTipoEspecificoRemote.find(idSgTipoEspecifico));
	sgGastoViaje.setModifico(new Usuario(idUsuario));
	sgGastoViaje.setFechaModifico(new Date());
	sgGastoViaje.setHoraModifico(new Date());

	edit(sgGastoViaje);
	UtilLog4j.log.info(this, "SgGastoViaje CREATED SUCCESSFULLY");
    }

    
    public void delete(int idSgGastoViaje, String idUsuario) {
	UtilLog4j.log.info(this, "SgGastoViajeImpl.delete()");

	SgGastoViaje sgGastoViaje = find(idSgGastoViaje);
	sgGastoViaje.setModifico(new Usuario(idUsuario));
	sgGastoViaje.setFechaModifico(new Date());
	sgGastoViaje.setHoraModifico(new Date());
	sgGastoViaje.setEliminado(Constantes.ELIMINADO);

	edit(sgGastoViaje);
	UtilLog4j.log.info(this, "SgGastoViaje DELETED SUCCESSFULLY");
    }

    
    public void addSiAdjuntoToSgGastoViaje(int idSgGastoViaje, int idSiAdjunto, String idUsuario) {
	UtilLog4j.log.info(this, "SgGastoViajeImpl.addSiAdjunto()");

	SgGastoViaje sgGastoViaje = find(idSgGastoViaje);
	sgGastoViaje.setSiAdjunto(this.siAdjuntoRemote.find(idSiAdjunto));
	sgGastoViaje.setModifico(new Usuario(idUsuario));
	sgGastoViaje.setFechaModifico(new Date());
	sgGastoViaje.setHoraModifico(new Date());

	edit(sgGastoViaje);
	UtilLog4j.log.info(this, "SgGastoViaje UPDATED SUCCESSFULLY");
    }

    
    public void deleteSiAdjuntoFromSgGastoViaje(int idSgGastoViaje, int idSiAdjunto, String idUsuario) {
	UtilLog4j.log.info(this, "SgGastoViajeImpl.deleteSiAdjunto()");

	SgGastoViaje sgGastoViaje = find(idSgGastoViaje);
	sgGastoViaje.setSiAdjunto(null);
	sgGastoViaje.setModifico(new Usuario(idUsuario));
	sgGastoViaje.setFechaModifico(new Date());
	sgGastoViaje.setHoraModifico(new Date());

	edit(sgGastoViaje);
	this.siAdjuntoRemote.delete(idSiAdjunto, idUsuario);

	UtilLog4j.log.info(this, "SgGastoViaje DELETED SUCCESSFULLY");
    }

    
    public List<SgGastoViajeVO> findAllSgGastoViajeBySgViajeNative(int idSgViaje, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SgGastoViajeImpl.findAllSgGastoViajeBySgViaje()");

	String q = "SELECT "
		+ "gv.id, " //0
		+ "gv.observacion, " //1
		+ "gv.importe, " //2
		+ "m.NOMBRE AS nombre_moneda, " //3
		+ "m.ID AS id_moneda, " //4
		+ "te.NOMBRE AS nombre_tipo_especifico, " //5
		+ "te.ID AS id_tipo_especifico, " //6
		+ "v.CODIGO AS codigo_viaje, " //7
		+ "CASE WHEN gv.SI_ADJUNTO IS NULL THEN -1 " //
		+ "WHEN gv.SI_ADJUNTO IS NOT NULL THEN gv.SI_ADJUNTO " //
		+ "END AS id_adjunto, " //8
		+ " CASE WHEN gv.SI_ADJUNTO is null THEN ''"
		+ " WHEN gv.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = gv.si_adjunto)   "
		+ " END AS uuid_adjunto"//9

		+ "FROM SG_GASTO_VIAJE gv, SG_TIPO_ESPECIFICO te, MONEDA m, SG_VIAJE v "
		+ "WHERE "
		+ "gv.SG_VIAJE = " + idSgViaje + " "
		+ "AND gv.SG_VIAJE = v.id "
		+ "AND gv.MONEDA = m.ID "
		+ "AND gv.SG_TIPO_ESPECIFICO = te.ID "
		+ "AND gv.eliminado = '" + Constantes.NO_ELIMINADO + "'";

	if (orderByField != null && !orderByField.isEmpty()) {
	    q += " ORDER BY gv." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	}

	Query consulta = em.createNativeQuery(q);

//        UtilLog4j.log.info(this, "query: " + query.toString());
	List<Object[]> result = consulta.getResultList();
	List<SgGastoViajeVO> list = new ArrayList<SgGastoViajeVO>();

	SgGastoViajeVO vo = null;

	for (Object[] objects : result) {
	    vo = new SgGastoViajeVO();
	    vo.setId((Integer) objects[0]);
	    vo.setObservacion((String) objects[1]);
	    vo.setImporte((BigDecimal) objects[2]);
	    vo.setNombreMoneda((String) objects[3]);
	    vo.setIdMoneda((Integer) objects[4]);
	    vo.setNombreSgTipoEspecifico((String) objects[5]);
	    vo.setIdSgTipoEspecifico((Integer) objects[6]);
	    vo.setCodigoSgViaje((String) objects[7]);
	    vo.setIdSiAdjunto((Integer) objects[8]);
	    vo.setUuid((String) objects[9]);
	    list.add(vo);
	}

	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgGastoViaje");

	return (list != null ? list : Collections.EMPTY_LIST);
    }
}
