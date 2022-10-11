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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.modelo.SgOficina;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioOficina;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sgl.pago.vo.PagoServicioVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgPagoServicioOficinaImpl extends AbstractFacade<SgPagoServicioOficina> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgPagoServicioOficinaImpl() {
	super(SgPagoServicioOficina.class);
    }

    
    public void guardarRelacionPago(SgPagoServicio sgPagoServicio, int idOficina, Usuario usuario, boolean eliminado) {
	SgPagoServicioOficina sgPagoServicioOficina = new SgPagoServicioOficina();
	sgPagoServicioOficina.setSgOficina(new SgOficina(idOficina));
	sgPagoServicioOficina.setSgPagoServicio(sgPagoServicio);
	sgPagoServicioOficina.setGenero(usuario);
	sgPagoServicioOficina.setFechaGenero(new Date());
	sgPagoServicioOficina.setHoraGenero(new Date());
	sgPagoServicioOficina.setEliminado(eliminado);
	create(sgPagoServicioOficina);

    }

    
    public List<SgPagoServicioOficina> traerPagoPorTipoEspecifico(SgTipo sgTipo, SgTipoEspecifico sgTipoEspecifico, int sgOficina, boolean eliminado) {
	try {
	    return em.createQuery("SELECT ps FROM SgPagoServicioOficina ps WHERE ps.sgOficina.id = :idOficina"
		    + " AND ps.sgPagoServicio.sgTipo.id = :idTipo AND ps.sgPagoServicio.sgTipoEspecifico.id = :idTipoEspecifico "
		    + " AND ps.eliminado = :eli ORDER BY ps.id DESC").setMaxResults(12)
		    .setParameter("idOficina", sgOficina)
		    .setParameter("idTipo", sgTipo.getId())
		    .setParameter("idTipoEspecifico", sgTipoEspecifico.getId())
		    .setParameter("eli", eliminado)
		    .getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public void eliminarRelacionPagoServicio(SgPagoServicioOficina sgPagoServicioOficina, Usuario usuario, boolean eliminado) {
//////         SgPagoServicioOficina sgPagoServicioOficina = buscarPorPagoServicio(sgPagoServicio);
	if (sgPagoServicioOficina != null) {
	    sgPagoServicioOficina.setGenero(usuario);
	    sgPagoServicioOficina.setFechaGenero(new Date());
	    sgPagoServicioOficina.setHoraGenero(new Date());
	    sgPagoServicioOficina.setEliminado(eliminado);
	    edit(sgPagoServicioOficina);
	}
    }

    
    public List<PagoServicioVo> pagoServicioOficinaPorAnio(int anio) {
	List<PagoServicioVo> lp = new ArrayList<PagoServicioVo>();
	String q = "select o.id,  o.NOMBRE, sum(ps.IMPORTE) "
		+ "	from SG_PAGO_SERVICIO_OFICINA pss"
		+ "	inner join SG_PAGO_SERVICIO ps on pss.SG_PAGO_SERVICIO = ps.ID"
		+ "	inner join SG_OFICINA o on pss.SG_OFICINA = o.ID"
		+ "	where o.eliminado = 'False'";
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

    
    public List<PagoServicioVo> traerTotalPagoOficina(int idOficina, int anio) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select  te.nombre,   sum(ps.IMPORTE)");
	query.append(" from SG_PAGO_SERVICIO_OFICINA pss");
	query.append("      inner join SG_PAGO_SERVICIO ps on pss.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	query.append("  where pss.sg_oficina = ").append(idOficina).append(" and ps.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	if (anio > 0) {
	    query.append("  and extract(year from ps.fecha_inicio) = ").append(anio);
	}
	query.append("      group by te.NOMBRE order by te.NOMBRE asc");

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

    
    public List<PagoServicioVo> traerPagoServicio(int oficina, String servicio, int anio) {
	clearQuery();
	List<PagoServicioVo> lr = null;
	query.append("select  te.NOMBRE, ps.FECHA_INICIO, ps.FECHA_FIN, ps.FECHA_VENCIMIENTO, ps.IMPORTE, m.SIGLAS, ");
	query.append(" 	ps.NUMERO_FACTURA, ps.SI_ADJUNTO, '").append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W='||ps.SI_ADJUNTO || '&ZWZ3W=' || adj.UUID ");
	query.append(" from SG_PAGO_SERVICIO_OFICINA pss");
	query.append("      inner join SG_PAGO_SERVICIO ps on pss.SG_PAGO_SERVICIO = ps.ID");
	query.append("      inner join SG_TIPO_ESPECIFICO te on ps.SG_TIPO_ESPECIFICO = te.ID");
	query.append("      inner join MONEDA  m on ps.MONEDA = m.id");
	query.append("      left join si_adjunto adj on ps.si_adjunto = adj.ID");
	query.append("  where pss.sg_oficina = ").append(oficina);
	query.append("  and te.nombre = '").append(servicio).append("'");
	if (anio > 0) {
	    query.append("  and extract(year from ps.fecha_inicio) = ").append(anio);
	}
	query.append("  and pss.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  and ps.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append("  order by ps.fecha_inicio asc");
        //
        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

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
}
