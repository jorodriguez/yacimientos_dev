/*
 * TipoObraImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.TipoObra;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.vo.RelProyectoTipoObraVO;
import sia.servicios.requisicion.impl.RelProyectoTipoObraImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505
 * @gmail.com
 * @date 7/07/2009
 */
@LocalBean 
public class TipoObraImpl {

    @Inject
    private RelProyectoTipoObraImpl relProyectoTipoObraServicioImpl;
    @PersistenceContext
    private EntityManager em;

    public void create(TipoObra tipoObra) {
        em.persist(tipoObra);
    }

    public void edit(TipoObra tipoObra) {
        em.merge(tipoObra);
    }

    public void remove(TipoObra tipoObra) {
        em.remove(em.merge(tipoObra));
    }

    public TipoObra find(Object id) {
        return em.find(TipoObra.class, id);
    }

    
    public List<TipoObra> findAll() {
        return em.createQuery("select object(o) from TipoObra as o WHERE o.visible = :visible ORDER BY o.nombre ASC").setParameter("visible", true).getResultList();
    }

    
    public TipoObra buscarPorNombre(Object nombreObra) {
        try {
            return (TipoObra) em.createQuery("SELECT o FROM TipoObra as o WHERE o.nombre = :nombre and o.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("nombre", nombreObra).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public List<TipoObra> getPorProyectoOt(Object nombreProyectoOt) {
        return relProyectoTipoObraServicioImpl.getPorProyectoOt(nombreProyectoOt);
    }

    
    public List<RelProyectoTipoObraVO> trarTipoObraPorProyectoOTid(int idProyectoOt) {
        return relProyectoTipoObraServicioImpl.traerPorProyectoId(idProyectoOt);
    }

    
    public List<Vo> traerTipoObraActiva() {
        //Utilizo el objeto Vo por que ese objeto es el que continee los atributos generales
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ID,  NOMBRE  FROM TIPO_OBRA");
            sb.append(" WHERE eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            sb.append(" order by nombre asc");
            List<Vo> le = new ArrayList<Vo>();
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] objects : lo) {
                Vo tipoObraVo = new Vo();
                tipoObraVo.setId((Integer) objects[0]);
                tipoObraVo.setNombre((String) objects[1]);
                le.add(tipoObraVo);
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            //
            return null;
        }
    }

    
    public List<TipoObra> traerTipoObraPorNombre(String obra) {
        return em.createQuery("SELECT f FROM TipoObra f WHERE f.nombre = :n ORDER BY f.nombre ASC").setParameter("n", obra).getResultList();
    }

    
    public void guardarTipoObra(String nombre) {
        TipoObra tipoObra = new TipoObra();
//        tipoObra.setId(this.folioServicioRemoto.getFolio("tipo_obra"));
        tipoObra.setNombre(nombre);
        tipoObra.setVisible(true);
        tipoObra.setEliminado(Constantes.NO_ELIMINADO);
        this.create(tipoObra);
    }
}
