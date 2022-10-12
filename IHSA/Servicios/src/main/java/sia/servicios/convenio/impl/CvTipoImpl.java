/*
 * To change this template, choose Tools | Templates
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
import sia.modelo.CvTipo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.vo.CatalogoContratoVo;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class CvTipoImpl extends AbstractFacade<CvTipo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CvTipoImpl() {
	super(CvTipo.class);
    }

    
    public List<CatalogoContratoVo> traerTodo() {
	List<CatalogoContratoVo> lvo = null;
	String cad = "select ct.id, ct.nombre, ct.descripcion from cv_tipo ct where ct.eliminado = '" + Constantes.NO_ELIMINADO + "'";
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
     * @param nombre
     * @param descripcion
     */
    
    public void guardar(String sesion, String nombre, String descripcion) {
	CvTipo cvTipo = new CvTipo();
	cvTipo.setNombre(nombre);
	cvTipo.setDescripcion(nombre);
	cvTipo.setGenero(new Usuario(sesion));
	cvTipo.setFechaGenero(new Date());
	cvTipo.setHoraGenero(new Date());
	cvTipo.setEliminado(Constantes.NO_ELIMINADO);
	create(cvTipo);
    }

    /**
     *
     * @param sesion
     * @param vo
     */
    
    public void modificar(String sesion, int idTipo, String nombre, String descripcion) {
	CvTipo cvTipo = find(idTipo);
	cvTipo.setNombre(nombre);
	cvTipo.setDescripcion(descripcion);
	cvTipo.setModifico(new Usuario(sesion));
	cvTipo.setFechaModifico(new Date());
	cvTipo.setHoraModifico(new Date());
	edit(cvTipo);
    }

    /**
     *
     * @param sesion
     * @param idTipo
     */
    
    public void eliminar(String sesion, int idTipo) {
	CvTipo cvTipo = find(idTipo);
	cvTipo.setModifico(new Usuario(sesion));
	cvTipo.setFechaModifico(new Date());
	cvTipo.setHoraModifico(new Date());
	cvTipo.setEliminado(Constantes.ELIMINADO);
	edit(cvTipo);
    }

    
    public boolean isUsado(int id) {
	List<Object[]> lo = em.createNativeQuery("select * from CONVENIO c where c.CV_TIPO = " + id + " and  c.eliminado = 'False'").getResultList();
	return !lo.isEmpty();
    }

    
    public CatalogoContratoVo buscarPorId(int id) {
	CatalogoContratoVo lvo = null;
	try {

	    String cad = "SELECT d.id, d.nombre, d.descripcion FROM CV_TIPO d where d.id = " + id + " and  d.eliminado = 'False' ";
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

}
