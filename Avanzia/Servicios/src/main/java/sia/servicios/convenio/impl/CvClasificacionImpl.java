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
import sia.modelo.CvClasificacion;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author ihsa
 */
@Stateless 
public class CvClasificacionImpl extends AbstractFacade<CvClasificacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CvClasificacionImpl() {
	super(CvClasificacion.class);
    }

    
    public List<ClasificacionVo> traerClasificacionPrincipal() {
	List<ClasificacionVo> lvo = null;
	String cad = "select ct.id, ct.nombre, ct.descripcion from cv_clasificacion ct where ct.cv_clasificacion is null and  ct.eliminado = '" + Constantes.NO_ELIMINADO + "'";
	List<Object[]> lo = em.createNativeQuery(cad).getResultList();
	if (lo != null) {
	    lvo = new ArrayList<ClasificacionVo>();
	    for (Object[] lo1 : lo) {
		ClasificacionVo vo = new ClasificacionVo();
		vo.setId((Integer) lo1[0]);
		vo.setNombre((String) lo1[1]);
		vo.setDescripcion((String) lo1[2]);
		lvo.add(vo);
	    }
	}
	return lvo;
    }

    
    public List<ClasificacionVo> traerPorClasificacion(int idClasificacion) {
	List<ClasificacionVo> lvo = null;
	String cad = "select ct.id, ct.nombre, ct.descripcion, ct.cv_clasificacion from cv_clasificacion ct where ct.cv_clasificacion = " + idClasificacion + " and  ct.eliminado = '" + Constantes.NO_ELIMINADO + "'";
	List<Object[]> lo = em.createNativeQuery(cad).getResultList();
	if (lo != null) {
	    lvo = new ArrayList<ClasificacionVo>();
	    for (Object[] lo1 : lo) {
		ClasificacionVo vo = new ClasificacionVo();
		vo.setId((Integer) lo1[0]);
		vo.setNombre((String) lo1[1]);
		vo.setDescripcion((String) lo1[2]);
		vo.setIdClasificacion(lo1[3] != null ? (Integer) lo1[3] : 0);
		lvo.add(vo);
	    }
	}
	return lvo;
    }

    /**
     *
     * @param sesion
     * @param nombre
     * @param descripcion
     * @param idClasificacion
     */
    
    public void guardar(String sesion, String nombre, String descripcion, int idClasificacion) {
	CvClasificacion cvClasificacion = new CvClasificacion();
	cvClasificacion.setNombre(nombre);
	cvClasificacion.setDescripcion(descripcion);
	cvClasificacion.setCvClasificacion(idClasificacion > 0 ? new CvClasificacion(idClasificacion) : null);
	cvClasificacion.setGenero(new Usuario(sesion));
	cvClasificacion.setFechaGenero(new Date());
	cvClasificacion.setHoraGenero(new Date());
	cvClasificacion.setEliminado(Constantes.NO_ELIMINADO);
	create(cvClasificacion);
    }

    /**
     *
     * @param sesion
     * @param idTipo
     * @param nombre
     * @param descripcion
     */
    
    public void modificar(String sesion, int idTipo, String nombre, String descripcion) {
	CvClasificacion cvClasificacion = find(idTipo);
	cvClasificacion.setNombre(nombre);
	cvClasificacion.setDescripcion(descripcion);
	cvClasificacion.setModifico(new Usuario(sesion));
	cvClasificacion.setFechaModifico(new Date());
	cvClasificacion.setHoraModifico(new Date());
	edit(cvClasificacion);
    }

    /**
     *
     * @param sesion
     * @param idTipo
     */
    
    public void eliminar(String sesion, int idTipo) {
	CvClasificacion cvClasificacion = find(idTipo);
	cvClasificacion.setModifico(new Usuario(sesion));
	cvClasificacion.setFechaModifico(new Date());
	cvClasificacion.setHoraModifico(new Date());
	cvClasificacion.setEliminado(Constantes.ELIMINADO);
	edit(cvClasificacion);
    }

    
    public boolean isUsado(int id) {
	List<Object[]> lo = em.createNativeQuery("select * from CONVENIO c where c.CV_CLASIFICACION = " + id + " and  c.eliminado = 'False'").getResultList();
	return !lo.isEmpty();
    }

    
    public ClasificacionVo buscarPorId(int id) {
	ClasificacionVo lvo = null;
	try {

	    String cad = "SELECT d.id, d.nombre, d.descripcion, d.cv_clasificacion FROM CV_CLASIFICACION d where d.id = " + id + " and  d.eliminado = 'False' ";
	    Object[] lo = (Object[]) em.createNativeQuery(cad).getSingleResult();
	    if (lo != null) {
		lvo = new ClasificacionVo();
		lvo.setId((Integer) lo[0]);
		lvo.setNombre((String) lo[1]);
		lvo.setDescripcion((String) lo[2]);
		lvo.setIdClasificacion(lo[3] != null ? (Integer) lo[3] : 0);
	    }
	} catch (Exception e) {
	}
	return lvo;
    }

    
    public List<ClasificacionVo> traerClasificaciones() {
	String con = "WITH RECURSIVE arbol_categorias AS (\n"
		+ "        SELECT id,\n"
		+ "               lpad(cast(id as varchar(2)), 2, '0')||'-'||CAST(nombre As varchar(1000)) As nombre \n"
		+ "            , CV_CLASIFICACION \n"
		+ "        FROM CV_CLASIFICACION \n"
		+ "        WHERE CV_CLASIFICACION IS NULL"
		+ "	   and eliminado =  'False' \n"
		+ "UNION ALL\n"
		+ "        SELECT si.id,\n"
		+ "                CAST(sp.nombre || '->' || lpad(cast(si.id as varchar(2)), 2, '0')||'-'||si.nombre As varchar(1000)) As nombre \n"
		+ "                ,si.CV_CLASIFICACION \n"
		+ "        FROM CV_CLASIFICACION As si \n"
		+ "                INNER JOIN arbol_categorias AS sp \n"
		+ "                ON (si.cv_clasificacion = sp.id) and si.eliminado = 'False' \n"
		+ ")\n"
		+ "SELECT id, nombre,cv_clasificacion  \n"
		+ "FROM arbol_categorias \n"
		+ "ORDER BY nombre";
	List<Object[]> lo = em.createNativeQuery(con).getResultList();
	//
	List<ClasificacionVo> clasificacionVo = new ArrayList<ClasificacionVo>();
	for (Object[] lo1 : lo) {
	    ClasificacionVo c = new ClasificacionVo();
	    c.setId((Integer) lo1[0]);
	    c.setNombre((String) lo1[1]);
	    c.setIdClasificacion(lo1[2] != null ? (Integer) lo1[2] : 0);
	    clasificacionVo.add(c);
	}
	return clasificacionVo;
    }
}
