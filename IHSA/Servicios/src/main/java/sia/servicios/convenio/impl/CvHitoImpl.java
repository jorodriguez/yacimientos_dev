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
import sia.modelo.CvHito;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.CatalogoContratoVo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvHitoImpl extends AbstractFacade<CvHito> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CvHitoImpl() {
	super(CvHito.class);
    }

    
    public List<CatalogoContratoVo> traerTodo() {
	List<CatalogoContratoVo> lvo = null;
	String cad = "select ct.id, ct.nombre, ct.descripcion from cv_hito ct where ct.eliminado = '" + Constantes.NO_ELIMINADO + "'";
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

    /**
     *
     * @param sesion
     * @param vo
     */
    
    public void guardar(String sesion, String nombre, String descripcion) {
	CvHito cvHito = new CvHito();
	cvHito.setNombre(nombre);
	cvHito.setDescripcion(descripcion);
	cvHito.setGenero(new Usuario(sesion));
	cvHito.setFechaGenero(new Date());
	cvHito.setHoraGenero(new Date());
	cvHito.setEliminado(Constantes.NO_ELIMINADO);
	create(cvHito);
    }

    /**
     *
     * @param sesion
     * @param vo
     */
    
    public void modificar(String sesion, int idTipo, String nombre, String descripcion) {
	CvHito cvHito = find(idTipo);
	cvHito.setNombre(nombre);
	cvHito.setDescripcion(descripcion);
	cvHito.setModifico(new Usuario(sesion));
	cvHito.setFechaModifico(new Date());
	cvHito.setHoraModifico(new Date());
	edit(cvHito);
    }

    /**
     *
     * @param sesion
     * @param vo
     */
    
    public void eliminar(String sesion, int idTipo) {
	CvHito cvHito = find(idTipo);
	cvHito.setModifico(new Usuario(sesion));
	cvHito.setFechaModifico(new Date());
	cvHito.setHoraModifico(new Date());
	cvHito.setEliminado(Constantes.ELIMINADO);
	edit(cvHito);
    }

    
    public boolean isUsado(int id) {
	List<Object[]> lo = em.createNativeQuery("select * from CV_CONVENIO_HITO ch where ch.CV_HITO = " + id + " and ch.eliminado = 'False'").getResultList();
	return !lo.isEmpty();
    }

    
    public CatalogoContratoVo buscarPorId(int id) {
	CatalogoContratoVo lvo = null;
	try {

	    String cad = "SELECT d.id, d.nombre, d.descripcion FROM CV_HITO d where d.id = " + id + " and  d.eliminado = 'False' ";
	    Object[] lo = (Object[]) em.createNativeQuery(cad).getSingleResult();
	    if (lo != null) {
		lvo = new CatalogoContratoVo();
		lvo.setId((Integer) lo[0]);
		lvo.setNombre((String) lo[1]);
		lvo.setDescripcion((String) lo[2]);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	}
	return lvo;
    }

    
    public List<CatalogoContratoVo> traerHitoFaltante(int idConvenio) {
	List<CatalogoContratoVo> lvo = null;
	String cad = "SELECT d.id, d.nombre, d.descripcion FROM cv_hito d ";
	cad += " where d.eliminado = 'False' ";
	cad += " and d.ID not in (select cd.cv_hito from CV_CONVENIO_hito cd where cd.CONVENIO = ";
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
