/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SiCategoriaIncidencia;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.CategoriaIncidenciaVo;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SiCategoriaIncidenciaImpl extends AbstractFacade<SiCategoriaIncidencia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiCategoriaIncidenciaImpl() {
        super(SiCategoriaIncidencia.class);
    }

    
    public List<SiCategoriaIncidencia> traerTodo() {
        return em.createNamedQuery("SiCategoriaIncidencia.findAll").getResultList();
    }

    
    public CategoriaIncidenciaVo buscarPorId(int idCatInc) {
        String c = "select id, nombre, tabla, campo_tabla from si_categoria_incidencia ci where ci.id = " + idCatInc;
        try {
            Object[] obj = (Object[]) em.createNativeQuery(c).getSingleResult();
            CategoriaIncidenciaVo cat = new CategoriaIncidenciaVo();
            cat.setId((Integer) obj[0]);
            cat.setNombre((String) obj[1]);
            cat.setTabla((String) obj[2]);
            cat.setCampoTabla((String) obj[3]);
            return cat;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

}
