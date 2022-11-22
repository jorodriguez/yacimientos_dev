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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgTipoTipoEspecificoImpl extends AbstractFacade<SgTipoTipoEspecifico>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgTipoTipoEspecificoImpl() {
	super(SgTipoTipoEspecifico.class);
    }

    
    public void guardarRelacionTipoTipoEspecifico(int sgTipo, SgTipoEspecifico sgTipoEspecifico, Usuario usuario, boolean BOOLEAN_FALSE) {
	UtilLog4j.log.info(this, "SgTipoTipoEspecificoImpl.guardarRelacionTipoTipoEspecifico()");

	SgTipoTipoEspecifico sgTipoTipoEspecifico = new SgTipoTipoEspecifico();
	sgTipoTipoEspecifico.setSgTipo(new SgTipo(sgTipo));
	sgTipoTipoEspecifico.setSgTipoEspecifico(sgTipoEspecifico);
	sgTipoTipoEspecifico.setGenero(usuario);
	sgTipoTipoEspecifico.setFechaGenero(new Date());
	sgTipoTipoEspecifico.setHoraGenero(new Date());
	sgTipoTipoEspecifico.setEliminado(BOOLEAN_FALSE);
	create(sgTipoTipoEspecifico);
    }

    
    public void eliminarRelacionTipoEspecifico(SgTipoTipoEspecifico sgTipoTipoEspecifico, Usuario usuario, boolean BOOLEAN_TRUE) {
	sgTipoTipoEspecifico.setGenero(usuario);
	sgTipoTipoEspecifico.setFechaGenero(new Date());
	sgTipoTipoEspecifico.setHoraGenero(new Date());
	sgTipoTipoEspecifico.setEliminado(BOOLEAN_TRUE);
	edit(sgTipoTipoEspecifico);
    }

    
    public void modificarRelacionTipoEspecifico(SgTipoTipoEspecifico sgTipoTipoEspecifico, Usuario usuario, boolean eliminado) {
	sgTipoTipoEspecifico.setGenero(usuario);
	sgTipoTipoEspecifico.setFechaGenero(new Date());
	sgTipoTipoEspecifico.setHoraGenero(new Date());
	sgTipoTipoEspecifico.setEliminado(eliminado);
	edit(sgTipoTipoEspecifico);
    }

    
    public SgTipoTipoEspecifico buscarPorTipoPorTipoEspecifico(int sgTipo, SgTipoEspecifico sgTipoEspecifico) {
	try {
	    return (SgTipoTipoEspecifico) em.createQuery("SELECT tt FROM SgTipoTipoEspecifico tt WHERE tt.sgTipo.id = :idTipo AND tt.sgTipoEspecifico.id = :idTipoEsp").setParameter("idTipo", sgTipo).setParameter("idTipoEsp", sgTipoEspecifico.getId()).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción al buscar la relación entre un Tipo y un Tipo Específico");
	    return null;
	}
    }

    
    public List<SgTipoTipoEspecifico> buscarPorTipoEspecifico(SgTipoEspecifico sgTipoEspecifico, boolean eliminado) {
	try {
	    return em.createQuery("SELECT tt FROM SgTipoTipoEspecifico tt WHERE tt.sgTipoEspecifico.id = :idTipoEsp AND tt.eliminado = :eli").setParameter("idTipoEsp", sgTipoEspecifico.getId()).setParameter("eli", eliminado).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public void modificarTipoEspecifico(int sgTipo, SgTipoEspecifico sgTipoEsp, Usuario usuario, boolean BOOLEAN_FALSE) {
	SgTipoTipoEspecifico sgTipoTipoEspecifico = buscarPorTipoPorTipoEspecifico(sgTipo, sgTipoEsp);
	sgTipoTipoEspecifico.setGenero(usuario);
	sgTipoTipoEspecifico.setFechaGenero(new Date());
	sgTipoTipoEspecifico.setHoraGenero(new Date());
	sgTipoTipoEspecifico.setEliminado(BOOLEAN_FALSE);
	edit(sgTipoTipoEspecifico);
    }

    
    public List<SgTipoTipoEspecifico> traerPorIdTipo(int idTipo, boolean eliminado) {
	try {
	    return em.createQuery("SELECT tt FROM SgTipoTipoEspecifico tt WHERE tt.sgTipo.id = :idTipo AND tt.eliminado = :eli  ORDER BY tt.id ASC").setParameter("eli", eliminado).setParameter("idTipo", idTipo).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public List<SgTipoTipoEspecifico> traerPorIdTipoAvisoStaff(int idTipo, int staff, boolean eliminado) {
	try {
	    return em.createQuery("SELECT tt FROM SgTipoTipoEspecifico tt "
		    + " WHERE tt.sgTipo.id = :idTipo AND tt.eliminado = :eli "
		    + " AND tt.id NOT IN (SELECT ps.sgAvisoPago.sgTipoEspecifico.id "
		    + " FROM SgAvisoPagoStaff ps WHERE ps.sgStaff.id = :idStaff AND ps.eliminado = :eli)").setParameter("idStaff", staff).setParameter("eli", eliminado).setParameter("idTipo", idTipo).getResultList();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public List<SgTipoTipoEspecifico> traerPorTipo(SgTipo sgTipo, boolean eliminado) {
	try {
	    return em.createQuery("SELECT tt FROM SgTipoTipoEspecifico tt WHERE tt.sgTipo.id = :idTipo AND tt.eliminado = :eli ORDER BY tt.id ASC").setParameter("eli", eliminado).setParameter("idTipo", sgTipo.getId()).getResultList();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public List<SgTipoTipoEspecifico> traerPorTipoPago(SgTipo sgTipo, boolean eliminado, boolean pago) {
	UtilLog4j.log.info(this, "SgTipoTipoEspecificoImpl.traerPorTipoPago()");

	List<SgTipoTipoEspecifico> tipoTipoEspecificoList = null;

	try {
	    tipoTipoEspecificoList = em.createQuery("SELECT tt FROM SgTipoTipoEspecifico tt "
		    + "WHERE tt.sgTipo.id = :idTipo "
		    + "AND tt.eliminado = :eli "
		    + "AND tt.sgTipoEspecifico.pago = :pago "
		    + "ORDER BY tt.id ASC").setParameter("pago", pago).setParameter("eli", eliminado).setParameter("idTipo", sgTipo.getId()).getResultList();

	    UtilLog4j.log.info(this, "Se encontraron " + tipoTipoEspecificoList.size() + "tipoEspecífico por tipo: " + sgTipo.getId() + " y pago: " + pago);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Error al traer los tipos específicos por tipo pago", e);
	}
	return tipoTipoEspecificoList;
    }

    
    public List<SgTipoTipoEspecifico> getSgTipoTipoEspecificoBySgTipo(int idSgTipo, boolean pago, boolean sistema, boolean eliminado) {
	UtilLog4j.log.info(this, "SgTipoTipoEspecificoImpl.getSgTipoTipoEspecificoBySgTipo()");

	List<SgTipoTipoEspecifico> list = null;

	list = em.createQuery("SELECT tte FROM SgTipoTipoEspecifico tte "
		+ "WHERE tte.sgTipo.id = :idSgTipo "
		+ "AND tte.eliminado = :eliminado "
		+ "AND tte.sgTipoEspecifico.sistema = :sistema "
		+ "AND tte.sgTipoEspecifico.pago = :pago "
		+ "ORDER BY tte.id ASC").setParameter("pago", pago ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).setParameter("sistema", sistema ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).setParameter("eliminado", eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).setParameter("idSgTipo", idSgTipo).getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + list.size() + "sgTipoEspecifico por tipo: " + idSgTipo + " y pago: " + pago + " y sistema: " + sistema);
	return list != null ? list : Collections.EMPTY_LIST;
    }

    
    public List<TipoEspecificoVo> traerPorTipo(int idTipo, boolean pago) {
	clearQuery();
	List<TipoEspecificoVo> tipoEspecificoList = null;

	try {
	    query.append("select te.id, te.nombre from Sg_Tipo_Tipo_Especifico tte ");
	    query.append("     inner join sg_tipo_especifico te on tte.sg_tipo_especifico = te.id");
	    query.append(" where tte.sg_tipo = ").append(idTipo);
	    query.append(" and tte.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and te.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and te.pago = '").append(pago).append("'");
	    query.append(" order by te.nombre asc");
	    List<Object[]> listTipo = em.createNativeQuery(query.toString()).getResultList();
	    if (listTipo != null) {
		tipoEspecificoList = new ArrayList<TipoEspecificoVo>();
		for (Object[] objects : listTipo) {
		    tipoEspecificoList.add(castTipoEspecifico(objects));
		}
	    }

	    return tipoEspecificoList;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Error al traer los tipos específicos por tipo pago");
	    return null;
	}
    }

    private TipoEspecificoVo castTipoEspecifico(Object[] objects) {
	TipoEspecificoVo tipoEspecificoVo = new TipoEspecificoVo();
	tipoEspecificoVo.setId((Integer) objects[0]);
	tipoEspecificoVo.setNombre((String) objects[1]);
	return tipoEspecificoVo;
    }

    
    public int buscarPorTipoEspecifico(int tipoEspecifico) {
	clearQuery();

	try {
	    query.append("select tte.id, tte.sg_tipo from Sg_Tipo_Tipo_Especifico tte ");
	    query.append(" where tte.sg_tipo_especifico = ").append(tipoEspecifico);
	    query.append(" and tte.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    Object[] listTipo = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

	    return (Integer) listTipo[1];
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Error al traer el tipo " + e.getMessage());
	    return 0;
	}
    }

    
    public List<TipoEspecificoVo> traerPorTipoEspecificoPorTipo(int idTipo) {
	clearQuery();
	List<TipoEspecificoVo> tipoEspecificoList = null;

	try {
	    query.append("select te.id, te.nombre from Sg_Tipo_Tipo_Especifico tte ");
	    query.append("     inner join sg_tipo_especifico te on tte.sg_tipo_especifico = te.id");
	    query.append(" where tte.sg_tipo = ").append(idTipo);
	    query.append(" and tte.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and te.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" order by te.nombre asc");
	    List<Object[]> listTipo = em.createNativeQuery(query.toString()).getResultList();
	    if (listTipo != null) {
		tipoEspecificoList = new ArrayList<TipoEspecificoVo>();
		for (Object[] objects : listTipo) {
		    tipoEspecificoList.add(castTipoEspecifico(objects));
		}
	    }

	    return tipoEspecificoList;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Error al traer los tipos específicos por tipo pago", e);
	    return null;
	}
    }
}
