/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lector.constantes.Constantes;
import lector.dominio.vo.RolVO;
import lector.modelo.SiModulo;
import lector.modelo.SiRol;
import lector.sistema.AbstractImpl;
import lector.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SiRolImpl extends AbstractImpl<SiRol>{


    public SiRolImpl() {
        super(SiRol.class);
    }

    
    public List<RolVO> traerRol(int idModulo) {
        clearQuery();
        List<RolVO> lr = new ArrayList<RolVO>();
        appendQuery("select r.id, r.nombre from si_rol r, si_modulo m ");
        appendQuery(" where r.si_modulo = ").append(idModulo);
        appendQuery(" and r.si_modulo =  m.id and r.eliminado =  '").append(Constantes.NO_ELIMINADO).append("'");
        appendQuery(" order by r.nombre asc");
        List<Object[]> objects = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] obj : objects) {
            lr.add(castRol(obj));
        }
        return lr;
    }

    
    public List<RolVO> traerRol(int idModulo, boolean inCategoria, boolean viewAll) {
        clearQuery();
        List<RolVO> lr = new ArrayList<RolVO>();
        appendQuery(" select r.id, r.nombre ");
        appendQuery(" from si_modulo m ");
        appendQuery(" inner join si_rol r on r.si_modulo =  m.id and r.eliminado =  'False' ");
        if (!viewAll) {
            appendQuery(" left join si_usuario_rol a on a.SI_ROL = r.ID and a.ELIMINADO = 'False' ");
        }
        appendQuery(" where r.si_modulo = ").append(idModulo);
        if (!viewAll && inCategoria) {
            appendQuery(" and a.ID is not null ");
        } else if (!viewAll) {
            appendQuery(" and a.ID is null ");
        }
        appendQuery(" and r.eliminado =  '").append(Constantes.NO_ELIMINADO).append("'");
        appendQuery(" group by r.id, r.nombre  ");
        appendQuery(" order by r.nombre asc ");
        List<Object[]> objects = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] obj : objects) {
            lr.add(castRol(obj));
        }
        return lr;
    }

    
    public List<RolVO> traerRol(int idModulo, int idRol, String nombreRol) {
        clearQuery();
        List<RolVO> lr = new ArrayList<RolVO>();
        appendQuery(" select r.id, r.nombre ");
        appendQuery(" from si_modulo m ");
        appendQuery(" inner join si_rol r on r.si_modulo =  m.id and r.eliminado =  'False' ");
        appendQuery(" where m.eliminado =  '").append(Constantes.NO_ELIMINADO).append("'");
        if (idModulo > 0) {
            appendQuery(" and m.id = ").append(idModulo);
        }

        if (idRol > 0) {
            appendQuery(" and r.id = ").append(idRol);
        } else if (nombreRol != null && !nombreRol.isEmpty()) {
            appendQuery(" and r.nombre = '").append(nombreRol).append("'");
        }
        appendQuery(" group by r.id, r.nombre  ");
        appendQuery(" order by r.nombre asc ");
        List<Object[]> objects = em.createNativeQuery(query.toString()).getResultList();
        for (Object[] obj : objects) {
            lr.add(castRol(obj));
        }
        return lr;
    }

    private RolVO castRol(Object[] objects) {
        RolVO rol = new RolVO();
        rol.setId((Integer) objects[0]);
        rol.setNombre((String) objects[1]);
        return rol;
    }

    
    public RolVO findRolByNombre(SiModulo mod, String rol) {
        Object[] ob = null;
        try {
            clearQuery();

            appendQuery("select r.id, r.nombre from si_rol r where ");
            if (mod != null) {
                appendQuery(" r.si_modulo = ").append(mod.getId());
                appendQuery(" and r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
                appendQuery(" and r.nombre = '").append(rol).append("'");
                ob = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            } else {
                appendQuery(" r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
                appendQuery(" and r.nombre = '").append(rol).append("'");
                ob = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
        return castRol(ob);
    }

    
    public SiRol buscarPorCodigo(String codigo) {
        try {
            return em.createNamedQuery("SiRol.findByCode", SiRol.class).setParameter(1, codigo).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }
}
