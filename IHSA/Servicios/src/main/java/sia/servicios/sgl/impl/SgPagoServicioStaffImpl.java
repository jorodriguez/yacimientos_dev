/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioStaff;
import sia.modelo.SgStaff;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sgl.pago.vo.PagoServicioVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiManejoFechaImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgPagoServicioStaffImpl extends AbstractFacade<SgPagoServicioStaff>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgPagoServicioStaffImpl() {
	super(SgPagoServicioStaff.class);
    }

    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    
    public void guardarRelacionPago(SgPagoServicio sgPagoServicio, SgStaff sgStaff, Usuario usuario, boolean eliminado) {
	SgPagoServicioStaff servicioStaffSgPagoServicioStaff = new SgPagoServicioStaff();
	servicioStaffSgPagoServicioStaff.setSgPagoServicio(sgPagoServicio);
	servicioStaffSgPagoServicioStaff.setSgStaff(sgStaff);
	servicioStaffSgPagoServicioStaff.setGenero(usuario);
	servicioStaffSgPagoServicioStaff.setFechaGenero(new Date());
	servicioStaffSgPagoServicioStaff.setHoraGenero(new Date());
	servicioStaffSgPagoServicioStaff.setEliminado(eliminado);
	create(servicioStaffSgPagoServicioStaff);

    }

    
    public List<SgPagoServicioStaff> traerPagoPorStaff(SgStaff sgStaff, SgTipo sgTipo, SgTipoEspecifico sgTipoEspecifico, boolean eliminado) {
	try {
	    return em.createQuery("SELECT ps FROM SgPagoServicioStaff ps WHERE ps.sgStaff.id = :idStaff"
		    + " AND ps.sgPagoServicio.sgTipo.id = :idTipo AND ps.sgPagoServicio.sgTipoEspecifico.id = :idTipoEspecifico "
		    + " AND ps.eliminado = :eli ORDER BY ps.id DESC").setMaxResults(5).setParameter("idStaff", sgStaff.getId()).setParameter("idTipo", sgTipo.getId()).setParameter("idTipoEspecifico", sgTipoEspecifico.getId()).setParameter("eli", eliminado).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public void eliminarRelacionPagoServicio(SgPagoServicioStaff sgPagoServicioStaff, Usuario usuario, boolean eliminado) {
//////        SgPagoServicioStaff sgPagoServicioStaff = buscarPorPagoServicio(sgPagoServicio);
	if (sgPagoServicioStaff != null) {
	    sgPagoServicioStaff.setGenero(usuario);
	    sgPagoServicioStaff.setFechaGenero(new Date());
	    sgPagoServicioStaff.setHoraGenero(new Date());
	    sgPagoServicioStaff.setEliminado(eliminado);
	    edit(sgPagoServicioStaff);
	}
    }

    private SgPagoServicioStaff buscarPorPagoServicio(SgPagoServicio sgPagoServicio) {
	try {
	    return (SgPagoServicioStaff) em.createQuery("SELECT sg FROM SgPagoServicioStaff sg WHERE sg.sgPagoSercvicio.id = :id AND sg.eliminado = :f ").setParameter("id", sgPagoServicio.getId()).setParameter("f", false).getSingleResult();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public List<PagoServicioVo> traerPago(int idStaff, int anio) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select  st.NOMBRE,  te.NOMBRE, te.id,  sum(ps.IMPORTE)");
	query.append(" from SG_PAGO_SERVICIO_STAFF pss");
	query.append("      inner join SG_PAGO_SERVICIO ps on pss.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	query.append("      inner join MONEDA  m on ps.MONEDA = m.id");
	query.append("      inner join SG_STAFF st on pss.SG_STAFF = st.ID");
	query.append("  where st.ID = ").append(idStaff);
	if (anio > 0) {
	    query.append("  and extract(year from ps.fecha_inicio) = ").append(anio);
	}
	query.append("  and st.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("      group by st.NOMBRE, te.NOMBRE, te.id order by st.NOMBRE asc");
	List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
	PagoServicioVo o;
	if (l != null || l.size() > 0) {
	    lr = new ArrayList<PagoServicioVo>();
	    for (Object[] objects : l) {
		o = new PagoServicioVo();
		o.setTipoEspecifico((String) objects[1]);
		o.setIdTipoEspecifico((Integer) objects[2]);
		BigDecimal bd = (BigDecimal) objects[3];
		o.setTotal((Double) bd.doubleValue());
		lr.add(o);
	    }
	}
	return lr;
    }

    
    public List<PagoServicioVo> traerPagoServicio(int idStaff, String servicio, int anio) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select  te.NOMBRE, ps.FECHA_INICIO, ps.FECHA_FIN, ps.FECHA_VENCIMIENTO, ps.IMPORTE, m.SIGLAS, ");
	query.append(" 	ps.NUMERO_FACTURA, ps.SI_ADJUNTO, '").append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W='||ps.SI_ADJUNTO || '&ZWZ3W=' || adj.UUID ");
	query.append(" from SG_PAGO_SERVICIO_STAFF pss");
	query.append("      inner join SG_PAGO_SERVICIO ps on pss.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	query.append("      inner join MONEDA  m on ps.MONEDA = m.id");
	query.append("      inner join SG_STAFF st on pss.SG_STAFF = st.ID");
	query.append("      left join si_adjunto adj on ps.si_adjunto = adj.ID");
	query.append("  where st.ID = ").append(idStaff);
	query.append("  and te.nombre = '").append(servicio).append("'");
	if (anio > 0) {
	    query.append("  and extract(year from ps.fecha_inicio) = ").append(anio);
	}
	query.append("  and st.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and pss.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and ps.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  order by ps.fecha_inicio asc");
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
	o.setTipoEspecifico((String) objects[0]);
	o.setInicio((Date) objects[1]);
	o.setFin((Date) objects[2]);
	o.setVencimiento((Date) objects[3]);
	BigDecimal bd = (BigDecimal) objects[4];
	o.setImporte(bd.doubleValue());
	o.setMoneda((String) objects[5]);
	o.setRecibo((String) objects[6]);
	o.setIdAdjunto(objects[7] != null ? (Integer) objects[7] : 0);
	o.setAdjuntoUUID((String) objects[8]);
	return o;
    }

    
    public List<PagoServicioVo> pagoServicioStaffPorAnio(int anio) {
	List<PagoServicioVo> lp = new ArrayList<PagoServicioVo>();
	String q = "select o.id,  o.NOMBRE, sum(ps.IMPORTE) "
		+ "	from SG_PAGO_SERVICIO_STAFF pss"
		+ "	inner join SG_PAGO_SERVICIO ps on pss.SG_PAGO_SERVICIO = ps.ID"
		+ "	inner join SG_STAFF st on pss.SG_STAFF = st.ID"
		+ "	inner join SG_OFICINA o on st.SG_OFICINA = o.ID"
		+ "	where st.eliminado = 'False'";
	if (anio > 0) {
	    q += "	and extract(year from ps.fecha_inicio) = " + anio;
	}
	q += "	group by o.ID, o.NOMBRE"
		+ "	order by o.id asc";

	List<Object[]> lo = em.createNativeQuery(q).getResultList();
	for (Object[] lo1 : lo) {
	    lp.add(castPagoServ(lo1));
	}
	return lp;
    }

    private PagoServicioVo castPagoServ(Object[] lo1) {
	PagoServicioVo ps = new PagoServicioVo();
	ps.getOficinaVO().setId((Integer) lo1[0]);
	ps.getOficinaVO().setNombre((String) lo1[1]);
	BigDecimal bd = (BigDecimal) lo1[2];
	ps.setTotal(bd.doubleValue());

	return ps;
    }

    
    public List<PagoServicioVo> traerTotalPagoStaff(int idOficina, int anio) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select  st.NOMBRE,   sum(ps.IMPORTE)");
	query.append(" from SG_PAGO_SERVICIO_STAFF pss");
	query.append("      inner join SG_PAGO_SERVICIO ps on pss.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_STAFF st on pss.SG_STAFF = st.ID");
	query.append("      inner join SG_OFICINA o on st.SG_OFICINA = o.ID");
	query.append("  where o.id = ").append(idOficina).append(" and st.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	if (anio > 0) {
	    query.append("  and extract(year from ps.fecha_inicio) = ").append(anio);
	}
	query.append("      group by st.NOMBRE order by st.NOMBRE asc");

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

}
