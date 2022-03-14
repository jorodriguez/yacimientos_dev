/*
 * UnidadFacade.java
 * Creada el 26/08/2009, 10:51:43 AM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Unidad;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.GeneralVo;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 26/08/2009
 */
@LocalBean 
public class UnidadImpl extends AbstractFacade<Unidad> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UnidadImpl() {
        super(Unidad.class);
    }

    //
    
    public List<GeneralVo> traerUnidad() {
        clearQuery();
        query.append("select u.id, u.nombre from unidad u where u.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
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

    private GeneralVo castUnidad(Object[] objects) {
        GeneralVo g = new GeneralVo();
        g.setValor((Integer) objects[0]);
        g.setNombre((String) objects[1]);
        return g;
    }
}
