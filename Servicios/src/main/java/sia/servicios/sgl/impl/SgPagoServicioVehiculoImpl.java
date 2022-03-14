/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgOficina;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioVehiculo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sgl.pago.vo.PagoServicioVo;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgPagoServicioVehiculoImpl extends AbstractFacade<SgPagoServicioVehiculo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }
    @Inject
    private SgKilometrajeImpl sgKilometrajeRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    public SgPagoServicioVehiculoImpl() {
	super(SgPagoServicioVehiculo.class);
    }

    
    public List<SgPagoServicioVehiculo> traerPagoPorVheiculo(int sgVehiculo, int sgTipo, SgTipoEspecifico sgTipoEspecifico, boolean eliminado) {
	try {
	    return em.createQuery("SELECT ps FROM SgPagoServicioVehiculo ps WHERE ps.sgVehiculo.id = :idVehiculo"
		    + " AND ps.sgPagoServicio.sgTipo.id = :idTipo AND ps.sgPagoServicio.sgTipoEspecifico.id = :idTipoEspecifico "
		    + " AND ps.eliminado = :eli ORDER BY ps.id DESC").setParameter("idVehiculo", sgVehiculo).setParameter("idTipo", sgTipo).setParameter("idTipoEspecifico", sgTipoEspecifico.getId()).setParameter("eli", eliminado).setMaxResults(5).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public void guardarRelacionPago(SgPagoServicio sgPagoServicio, int sgVehiculo, Usuario usuario, boolean eliminado) {
	UtilLog4j.log.info(this, "Pago Vehiculo");
	SgPagoServicioVehiculo sgPagoServicioVehiculo = new SgPagoServicioVehiculo();
	sgPagoServicioVehiculo.setSgPagoServicio(sgPagoServicio);
	sgPagoServicioVehiculo.setSgVehiculo(new SgVehiculo(sgVehiculo));
	sgPagoServicioVehiculo.setGenero(usuario);
	sgPagoServicioVehiculo.setFechaGenero(new Date());
	sgPagoServicioVehiculo.setHoraGenero(new Date());
	sgPagoServicioVehiculo.setEliminado(eliminado);
	create(sgPagoServicioVehiculo);

    }

    
    public void eliminarRelacionPagoServicio(SgPagoServicioVehiculo sgPagoServicioVehiculo, Usuario usuario, boolean eliminado) {
//////        SgPagoServicioVehiculo sgPagoServicioVehiculo = buscarPorPagoServicio(sgPagoServicio);
	if (sgPagoServicioVehiculo != null) {
	    sgPagoServicioVehiculo.setGenero(usuario);
	    sgPagoServicioVehiculo.setFechaGenero(new Date());
	    sgPagoServicioVehiculo.setHoraGenero(new Date());
	    sgPagoServicioVehiculo.setEliminado(eliminado);
	    edit(sgPagoServicioVehiculo);
	}
    }

    private SgPagoServicioVehiculo buscarPorPagoServicio(SgPagoServicio sgPagoServicio) {
	try {
	    return (SgPagoServicioVehiculo) em.createQuery("SELECT sg FROM SgPagoServicioVehiculo sg WHERE sg.sgPagoSercvicio.id = :id AND sg.eliminado = :f ").setParameter("id", sgPagoServicio.getId()).setParameter("f", false).getSingleResult();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public List<SgPagoServicioVehiculo> traerPagoPorVheiculo(int vehiculo) {
	try {
	    return em.createQuery("SELECT ps FROM SgPagoServicioVehiculo ps WHERE ps.sgVehiculo.id = :idVehiculo "
		    + " AND ps.eliminado = :eli "
		    + " ORDER BY ps.id DESC").setParameter("idVehiculo", vehiculo).setParameter("eli", Constantes.NO_ELIMINADO).setMaxResults(3).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public List<SgPagoServicioVehiculo> traerPagoVehiculoPorFechaVencimientoYOficina(SgOficina oficina, Date FechaVencimiento, SgTipoEspecifico tipoEspecifico) {
	UtilLog4j.log.info(this, "Tipo a buscar " + tipoEspecifico.getNombre());
	try {
	    return em.createQuery("SELECT ps FROM SgPagoServicioVehiculo ps "
		    + " WHERE ps.sgVehiculo.sgOficina = :oficina "
		    + " AND ps.sgVehiculo.eliminado = :eli "
		    + " AND ps.sgPagoServicio.fechaVencimiento = :fechaVencimiento "
		    + " AND ps.sgPagoServicio.sgTipo.id = :tipo"
		    + " AND ps.sgPagoServicio.sgTipoEspecifico.id = :tipoEspecifico"
		    + " AND ps.eliminado = :eli ").setParameter("oficina", oficina).setParameter("fechaVencimiento", FechaVencimiento).setParameter("tipo", 1) //<---Tipo Vehiculo (de SgTipo)
		    .setParameter("tipoEspecifico", tipoEspecifico.getId()).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en traerPago Vehiculo " + e.getMessage());
	    return null;
	}
    }

    
    public List<PagoServicioVo> buscarPagos(int idOficina, int idServicio) {
	clearQuery();
	List<PagoServicioVo> lr = new ArrayList<PagoServicioVo>();
	query.append(consulta(idServicio));
	query.append("  where v.SG_OFICINA = ").append(idOficina);
	if (idServicio > 0) {
	    query.append("    and te.id = ").append(idServicio);
	}
	//query.append("  and ps.FECHA_FIN between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
	query.append("  and ps.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and v.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("group by mod.NOMBRE");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null || l.size() > 0) {
	    for (Object[] objects : l) {
		o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lr.add(o);
	    }
	}
	return lr;
    }

    
    public List<PagoServicioVo> traerTotalPagoServioVehiculo(int idOficina, int servicio, int anio) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select mod.NOMBRE, sum(ps.IMPORTE) from SG_PAGO_SERVICIO_VEHICULO psv");
	query.append("      inner join SG_VEHICULO v on psv.SG_VEHICULO = v.ID");
	query.append("      inner join SG_MODELO mod on v.SG_MODELO = mod.ID");
	query.append("      inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	query.append("  where  v.SG_OFICINA = ").append(idOficina);
	query.append("  and te.id = ").append(servicio);
	if (anio > 0) {
	    query.append("  and extract(year from ps.FECHA_INICIO) = ").append(anio);
	}
	query.append("  and v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and psv.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and ps.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  group by mod.NOMBRE ");
	query.append(" order by mod.nombre asc");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null || l.size() > 0) {
	    lr = new ArrayList<PagoServicioVo>();
	    for (Object[] objects : l) {
		o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lr.add(o);
	    }
	}
	return lr;
    }

    
    public List<PagoServicioVo> traerPagoVehiculos(int idOficina, int servicio, int anio) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select mod.NOMBRE, te.NOMBRE, p.NOMBRE, ps.FECHA_INICIO, ps.FECHA_FIN, ps.FECHA_VENCIMIENTO, ps.IMPORTE, m.SIGLAS, ps.NUMERO_FACTURA, adj.ID, adj.UUID  from SG_PAGO_SERVICIO_VEHICULO psv");
	query.append("      inner join SG_VEHICULO v on psv.SG_VEHICULO = v.ID");
	query.append("      inner join SG_MODELO mod on v.SG_MODELO = mod.ID");
	query.append("      inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	query.append("      left join SI_ADJUNTO adj on ps.SI_ADJUNTO = adj.ID");
	query.append("      inner join PROVEEDOR p on ps.PROVEEDOR = p.ID");
	query.append("      inner join MONEDA m on ps.MONEDA = m.ID");
	query.append("  where  v.SG_OFICINA = ").append(idOficina);
	query.append("  and te.id = ").append(servicio);
	if (anio > 0) {
	    query.append("  and extract(year from ps.FECHA_INICIO) = ").append(anio);
	}
	query.append("  and v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and psv.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and ps.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" order by te.nombre asc");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null || l.size() > 0) {
	    lr = new ArrayList<PagoServicioVo>();
	    for (Object[] objects : l) {
		lr.add(castPagoServicio(objects));
	    }
	}
	return lr;
    }

    private PagoServicioVo castPagoServicio(Object[] objects) {
	PagoServicioVo o = new PagoServicioVo();
	o.setVehiculo((String) objects[0]);
	o.setTipoEspecifico((String) objects[1]);
	o.setProveedor((String) objects[2]);
	o.setInicio((Date) objects[3]);
	o.setFin((Date) objects[4]);
	o.setVencimiento((Date) objects[5]);
	BigDecimal bd = (BigDecimal) objects[6];
	o.setImporte(bd.doubleValue());
	o.setMoneda((String) objects[7]);
	o.setRecibo((String) objects[8]);
	o.setIdAdjunto(objects[9] != null ? (Integer) objects[9] : 0);
	o.setAdjuntoUUID((String) objects[10]);
	return o;
    }

    
    public List<TipoEspecificoVo> traerConceptosPago() {
	clearQuery();
	List<TipoEspecificoVo> lt = new ArrayList<TipoEspecificoVo>();
	query.append("select distinct(te.ID), te.NOMBRE from SG_PAGO_SERVICIO_VEHICULO psv ");
	query.append("      inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	try {
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		for (Object[] objects : lo) {
		    lt.add(castConceptos(objects));
		}
	    }
	    lt.addAll(sgKilometrajeRemote.traerConceptosPago(Constantes.TIPO_VEHICULO_MANTENIMIENTO));
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

    private String consulta(int idServicio) {
	StringBuilder sb = new StringBuilder();
	if (idServicio < 0) {
	    sb.append("select mod.NOMBRE, sum(ps.IMPORTE)+ sum(vm.IMPORTE)");
	} else {
	    sb.append("select mod.NOMBRE, sum(ps.IMPORTE)");
	}

	sb.append("  from SG_PAGO_SERVICIO_VEHICULO psv");
	sb.append("      inner join SG_VEHICULO v on psv.SG_VEHICULO = v.ID");
	sb.append("      inner join SG_MODELO mod on v.SG_MODELO = mod.ID");
	sb.append("      inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID");
	if (idServicio < 0) {
	    sb.append("      inner join SG_VEHICULO_MANTENIMIENTO vm on vm.SG_VEHICULO = v.ID");
	} else {
	    sb.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	}

	return sb.toString();
    }

    
    public List<PagoServicioVo> traerPagoVehiculosPorServicio(int servicio, int idOficina, String vehiculo) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select mod.NOMBRE, te.NOMBRE, p.NOMBRE, ps.FECHA_INICIO, ps.FECHA_FIN, ps.FECHA_VENCIMIENTO, ");
	query.append("	ps.IMPORTE, m.SIGLAS, ps.NUMERO_FACTURA, adj.ID, adj.UUID  from SG_PAGO_SERVICIO_VEHICULO psv");
	query.append("      inner join SG_VEHICULO v on psv.SG_VEHICULO = v.ID");
	query.append("      inner join SG_MODELO mod on v.SG_MODELO = mod.ID");
	query.append("      inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	query.append("      left join SI_ADJUNTO adj on ps.SI_ADJUNTO = adj.ID");
	query.append("      inner join PROVEEDOR p on ps.PROVEEDOR = p.ID");
	query.append("      inner join MONEDA m on ps.MONEDA = m.ID");
	query.append("  where  v.SG_OFICINA = ").append(idOficina);
	//query.append("  and ps.FECHA_FIN between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
	query.append("  and mod.NOMBRE = '").append(vehiculo).append("'");
	query.append("  and te.id = ").append(servicio);
	query.append("  and v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and psv.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and ps.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" order by ps.fecha_inicio  asc");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null && l.size() > 0) {
	    lr = new ArrayList<PagoServicioVo>();
	    for (Object[] objects : l) {
		lr.add(castPagoServicio(objects));
	    }
	}
	return lr;
    }

    
    public double totalImportePago(int idVehiculo, int idMoneda) {
	clearQuery();
	try {
	    query.append("select sum(ps.IMPORTE) from SG_PAGO_SERVICIO_VEHICULO psv ");
	    query.append("      inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID");
	    query.append("  where psv.SG_VEHICULO = ").append(idVehiculo);
	    query.append("  and ps.moneda =").append(idMoneda);
	    query.append("  and ps.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	    return ((Double) em.createNativeQuery(query.toString()).getSingleResult());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "no hay importes del vehiculo  + + + " + e.getMessage());
	    return 0.0;

	}

    }

    
    public List<PagoServicioVo> totalPagosVehiculos(int anio) {
	String q = " select o.NOMBRE, sum(ps.IMPORTE)    from SG_PAGO_SERVICIO_VEHICULO psv        "
		+ "	inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID"
		+ "	inner join SG_VEHICULO v on psv.SG_VEHICULO = v.ID"
		+ "	inner join SG_OFICINA o on v.SG_OFICINA = o.ID"
		+ " where o.ELIMINADO = 'False'";
	if (anio > 0) {
	    q += " and extract(year from ps.fecha_inicio) = " + anio;
	}
	q += " and psv.ELIMINADO = 'False' group by o.NOMBRE order by o.NOMBRE ";
	List<Object[]> l = em.createNativeQuery(q).getResultList();
	List<PagoServicioVo> lps = new ArrayList<PagoServicioVo>();
	if (l != null) {
	    for (Object[] objects : l) {
		PagoServicioVo o = new PagoServicioVo();
		o.getOficinaVO().setNombre((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lps.add(o);
	    }
	}
	return lps;
    }

    
    public List<PagoServicioVo> totalPagosVehiculosPorOficina(int oficina, int anio) {
	String q = " select te.NOMBRE, sum(ps.IMPORTE)    from SG_PAGO_SERVICIO_VEHICULO psv "
		+ "	inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID"
		+ "	inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.id"
		+ "	inner join SG_VEHICULO v on psv.SG_VEHICULO = v.ID"
		+ "	inner join SG_OFICINA o on v.SG_OFICINA = o.ID"
		+ " where o.id = " + oficina;
	if (anio > 0) {
	    q += " and extract(year from ps.fecha_inicio) = " + anio;
	}
	q += " and o.ELIMINADO = 'False' and psv.ELIMINADO = 'False' group by te.NOMBRE order by te.NOMBRE ";
	List<Object[]> l = em.createNativeQuery(q).getResultList();
	List<PagoServicioVo> lps = new ArrayList<PagoServicioVo>();
	if (l != null) {
	    for (Object[] objects : l) {
		PagoServicioVo o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lps.add(o);
	    }
	}
	return lps;
    }

    
    public List<PagoServicioVo> totalPagosVehiculosPorOficinaServicio(int oficina, int servicio, int anio) {
	String q = " select mo.NOMBRE, sum(ps.IMPORTE)    from SG_PAGO_SERVICIO_VEHICULO psv "
		+ "	inner join SG_PAGO_SERVICIO ps on psv.SG_PAGO_SERVICIO = ps.ID"
		+ "	inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.id"
		+ "	inner join SG_VEHICULO v on psv.SG_VEHICULO = v.ID"
		+ "	inner join SG_MODELO mo on v.SG_MODELO = mo.ID"
		+ " where v.sg_oficina = " + oficina
		+ " and te.id = " + servicio;
	if (anio > 0) {
	    q += " and extract(year from ps.fecha_inicio) = " + anio;
	}
	q += " and o.ELIMINADO = 'False'  and psv.ELIMINADO = 'False' group by mo.NOMBRE order by mo.NOMBRE ";
	List<Object[]> l = em.createNativeQuery(q).getResultList();
	List<PagoServicioVo> lps = new ArrayList<PagoServicioVo>();
	if (l != null) {
	    for (Object[] objects : l) {
		PagoServicioVo o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[0]);
		BigDecimal bd = (BigDecimal) objects[1];
		o.setTotal((Double) bd.doubleValue());
		lps.add(o);
	    }
	}
	return lps;
    }
}
