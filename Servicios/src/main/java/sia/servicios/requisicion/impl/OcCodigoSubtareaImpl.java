/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcCodigoSubtarea;
import sia.modelo.requisicion.vo.OcCodigoSubTareaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class OcCodigoSubtareaImpl extends AbstractFacade<OcCodigoSubtarea>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcCodigoSubtareaImpl() {
        super(OcCodigoSubtarea.class);
    }

    
    public int validarSubtareaExiste(String codigo, String nombre) {
        UtilLog4j.log.info(this, "#validarActividadExiste ");
        int existe = 0;
        try {
            String query = "select ID, CODIGO, NOMBRE from OC_CODIGO_SUBTAREA where ELIMINADO = false "
                    + " and upper(codigo) = upper('" + codigo + "') "
                    + " and upper(nombre) = upper('" + nombre + "') limit 1";

            UtilLog4j.log.info(this, "query" + query);

            Object[] lo = (Object[]) em.createNativeQuery(query).getSingleResult();

            if (lo != null) {
                existe = (Integer)lo[0];
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al validar la existencia de un codigo de subtarea " + e.getMessage(), e);
            existe = 0;

        }
        return existe;
    }

    
    public List<OcCodigoSubTareaVO> getCodigosSubtareas() {
        UtilLog4j.log.info(this, "#getCodigosSubtareas ");
        ArrayList<OcCodigoSubTareaVO> lst = new ArrayList<>();
        try {
            String consulta = " select id, codigo, nombre "
                    + " from oc_codigo_subtarea    "
                    + " where eliminado =  false  "
                    + " order by codigo ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();
            OcCodigoSubTareaVO vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    lst.add(new OcCodigoSubTareaVO((int) objects[0], (String) objects[1], (String) objects[2]));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde de las subtareas" + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public OcCodigoSubTareaVO getCodigoSubtarea(int idSubTarea) {
        UtilLog4j.log.info(this, "#getCodigoSubtarea ");
        OcCodigoSubTareaVO vo = null;
        try {
            String consulta = " select id, codigo, nombre "
                    + " from oc_codigo_subtarea    "
                    + " where eliminado =  false and id = " + idSubTarea
                    + " order by codigo ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();

            if (lo != null && lo.size() > 0) {
                Object[] objects = lo.get(0);
                vo = new OcCodigoSubTareaVO((int) objects[0], (String) objects[1], (String) objects[2]);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde de la subtarea" + e.getMessage(), e);
            vo = null;
        }
        return vo;
    }
}
