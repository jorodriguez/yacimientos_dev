package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.ProveedorVetado;
import sia.servicios.sistema.vo.ProveedorVetadoVO;
import sia.util.UtilLog4j;


@LocalBean 
public class ProveedorVetadoImpl {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(ProveedorVetado pv) {
        em.persist(pv);
    }

    
    public void edit(ProveedorVetado pv) {
        em.merge(pv);
    }

    
    public void remove(ProveedorVetado pv) {
        em.remove(em.merge(pv));
    }

    
    public ProveedorVetado find(Object id) {
        return em.find(ProveedorVetado.class, id);
    }

    
    public List<ProveedorVetado> findAll() {
        return em.createQuery("select object(o) from ProveedorVetado as o").getResultList();
    }

    
    
    public List<ProveedorVetadoVO> traerProveedoresVetados() {
        List<ProveedorVetadoVO> arrayVetados = null;
	try {
	    String qry = " select a.ID, a.RFC, a.NOMBRE, a.DESCRIPCION, a.ELIMINADO "+
                        " from PROVEEDOR_VETADO a ";	     

	    List<Object[]> lo = em.createNativeQuery(qry).getResultList();
            arrayVetados = new ArrayList<ProveedorVetadoVO>();
	    for (Object[] objects : lo) {
		ProveedorVetadoVO pv = new ProveedorVetadoVO();
		pv.setId((Integer) objects[0]);
		pv.setRfc((String) objects[1]);
		pv.setNombre((String) objects[2]);
                pv.setDescripcion((String) objects[3]);
		pv.setActivo((Boolean) objects[4]);
		arrayVetados.add(pv);
	    }	    
	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	    arrayVetados = null;
	}
        return arrayVetados;
    }

    
    public ProveedorVetadoVO findbyRfc(String rfc) {
         ProveedorVetadoVO vo = new ProveedorVetadoVO();
         
         try {
             StringBuilder sb= new StringBuilder();
             sb.append("select a.ID, a.RFC, a.NOMBRE, a.DESCRIPCION, a.ELIMINADO ")
               .append("from PROVEEDOR_VETADO a ")
               .append("where a.RFC=")
               .append("'")
               .append(rfc)
               .append("'")
               .append(" and a.ELIMINADO='False'");
	    
             Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
             if (obj != null) {
                vo.setId((Integer) obj[0]);
		vo.setRfc((String) obj[1]);
		vo.setNombre((String) obj[2]);
                vo.setDescripcion((String) obj[3]);
             }
            
	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
            vo= new ProveedorVetadoVO();
	}
         return vo;
    }
    
    
}
