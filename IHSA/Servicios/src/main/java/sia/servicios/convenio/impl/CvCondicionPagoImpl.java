/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CvCondicionPago;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.CatalogoContratoVo;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvCondicionPagoImpl extends AbstractFacade<CvCondicionPago> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CvCondicionPagoImpl() {
	super(CvCondicionPago.class);
    }

    
    public List<CatalogoContratoVo> traerTodo() {
	List<CatalogoContratoVo> lvo = null;
	String cad = "select ct.id, ct.nombre, ct.descripcion from cv_condicion_pago ct where ct.eliminado = '" + Constantes.NO_ELIMINADO + "'";
	List<Object[]> lo = em.createNativeQuery(cad).getResultList();
	if (lo != null) {
	    lvo = new ArrayList<CatalogoContratoVo>();
	    for (Object[] lo1 : lo) {
		CatalogoContratoVo vo = new CatalogoContratoVo();
		vo.setId((Integer) lo1[0]);
		vo.setNombre((String) lo1[1]);
		vo.setDescripcion((String) lo1[2]);
		lvo.add(vo);
	    }
	}
	return lvo;
    }

    
    public void guardar(String sesion, String nombre, String descripcion) {
	CvCondicionPago cvCondicionPago = new CvCondicionPago();
	cvCondicionPago.setNombre(nombre);
	cvCondicionPago.setDescripcion(descripcion);
	cvCondicionPago.setGenero(new Usuario(sesion));
	cvCondicionPago.setFechaGenero(new Date());
	cvCondicionPago.setHoraGenero(new Date());
	cvCondicionPago.setEliminado(Constantes.NO_ELIMINADO);
	create(cvCondicionPago);
    }

    
    public void modificar(String sesion, int idTipo, String nombre, String descripcion) {
	CvCondicionPago cvCondicionPago = find(idTipo);
	cvCondicionPago.setNombre(nombre);
	cvCondicionPago.setDescripcion(descripcion);
	cvCondicionPago.setModifico(new Usuario(sesion));
	cvCondicionPago.setFechaModifico(new Date());
	cvCondicionPago.setHoraModifico(new Date());
	edit(cvCondicionPago);
    }

    
    public void eliminar(String sesion, int idTipo) {
	CvCondicionPago cvCondicionPago = find(idTipo);
	cvCondicionPago.setModifico(new Usuario(sesion));
	cvCondicionPago.setFechaModifico(new Date());
	cvCondicionPago.setHoraModifico(new Date());
	cvCondicionPago.setEliminado(Constantes.ELIMINADO);
	edit(cvCondicionPago);
    }

    
    public boolean isUsado(int id) {
	List<Object[]> lo = em.createNativeQuery("select * from CV_CONVENIO_CONDICION_PAGO ccp where ccp.CV_CONDICION_PAGO = " + id + " and  ccp.eliminado = 'False'").getResultList();
	return !lo.isEmpty();
    }

    
    public CatalogoContratoVo buscarPorId(int id) {
	CatalogoContratoVo lvo = null;
	try {

	    String cad = "SELECT d.id, d.nombre, d.descripcion FROM CV_CONDICION_PAGO d where d.id = " + id + " and  d.eliminado = 'False' ";
	    Object[] lo = (Object[]) em.createNativeQuery(cad).getSingleResult();
	    if (lo != null) {
		lvo = new CatalogoContratoVo();
		lvo.setId((Integer) lo[0]);
		lvo.setNombre((String) lo[1]);
		lvo.setDescripcion((String) lo[2]);
	    }
	} catch (Exception e) {
	}
	return lvo;
    }

    
    public List<CatalogoContratoVo> traerCondicionFaltante(int idConvenio) {
	List<CatalogoContratoVo> lvo = null;
	String cad = "SELECT d.id, d.nombre, d.descripcion FROM cv_condicion_pago d ";
	cad += " where d.eliminado = 'False' ";
	cad += " and d.ID not in (select cd.cv_condicion_pago from CV_CONVENIO_CONDICION_PAGO cd where cd.CONVENIO = ";
	cad += " " + idConvenio + "	and cd.ELIMINADO = '" + Constantes.NO_ELIMINADO + "')";
	cad += " order by d.nombre ASC";
	List<Object[]> lo = em.createNativeQuery(cad).getResultList();
	if (lo != null) {
	    lvo = new ArrayList<CatalogoContratoVo>();
	    for (Object[] lo1 : lo) {
		CatalogoContratoVo vo = new CatalogoContratoVo();
		vo.setId((Integer) lo1[0]);
		vo.setNombre((String) lo1[1]);
		vo.setDescripcion((String) lo1[2]);
		vo.setSelected(false);
		lvo.add(vo);
	    }
	}
	return lvo;
    }
}
