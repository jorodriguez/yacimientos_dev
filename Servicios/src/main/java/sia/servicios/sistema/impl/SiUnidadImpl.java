/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiUnidad;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.GeneralVo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiUnidadImpl extends AbstractFacade<SiUnidad>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiUnidadImpl() {
        super(SiUnidad.class);
    }
//

    
    public List<GeneralVo> traerUnidad() {
        clearQuery();
        query.append("select u.id, u.nombre from si_unidad u where u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" order by u.nombre asc");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<GeneralVo> lg = null;
        if (lo != null) {
            lg = new ArrayList<GeneralVo>();
            for (Object[] objects : lo) {
                lg.add(castUnidad(objects));
            }
        }
        return lg;
    }

    
    public List<SelectItem> traerUnidadItems() {
        List<SelectItem> li = new ArrayList<SelectItem>();
        try {
            for (GeneralVo g : this.traerUnidad()) {
                li.add(new SelectItem(g.getNombre(), g.getNombre()));
            }

        } catch (Exception e) {
            li = new ArrayList<SelectItem>();
            UtilLog4j.log.info(this, "Ocurrio un error al traer las unidades # # # # # " + e.getMessage());
        }
        return li;
    }

    private GeneralVo castUnidad(Object[] objects) {
        GeneralVo g = new GeneralVo();
        g.setValor((Integer) objects[0]);
        g.setNombre((String) objects[1]);
        return g;
    }

    
    public SiUnidad buscarPorNombre(String nombre) {
        try {
            String sb = "select * from si_unidad where eliminado = '" + Constantes.NO_ELIMINADO + "' and upper(nombre) = upper('" + nombre + "')";
            return (SiUnidad) em.createNativeQuery(sb, SiUnidad.class).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
