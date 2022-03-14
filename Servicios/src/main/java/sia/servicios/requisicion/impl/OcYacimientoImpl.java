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
import sia.modelo.OcYacimiento;
import sia.modelo.requisicion.vo.OcCodigoSubTareaVO;
import sia.modelo.requisicion.vo.OcYacimientoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class OcYacimientoImpl extends AbstractFacade<OcYacimiento>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcYacimientoImpl() {
        super(OcYacimiento.class);
    }
    
    
    public List<OcYacimientoVO> getYacimientos() {
        UtilLog4j.log.info(this, "#getYacimientos ");
        ArrayList<OcYacimientoVO> lst = new ArrayList<>();
        try {
            String consulta = " select id, codigo, nombre "
                    + " from oc_yacimiento    "
                    + " where eliminado =  false  "
                    + " order by codigo ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();
            OcCodigoSubTareaVO vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    lst.add(new OcYacimientoVO((int) objects[0], (String) objects[1], (String) objects[2]));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde de las subtareas" + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public OcYacimientoVO getYacimiento(int idYacimiento) {
        UtilLog4j.log.info(this, "#getCodigoSubtarea ");
        OcYacimientoVO vo = null;
        try {
            String consulta = " select id, codigo, nombre "
                    + " from oc_yacimiento    "
                    + " where eliminado =  false and id = " + idYacimiento
                    + " order by codigo ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();

            if (lo != null && lo.size() > 0) {
                Object[] objects = lo.get(0);
                vo = new OcYacimientoVO((int) objects[0], (String) objects[1], (String) objects[2]);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde del yacimiento" + e.getMessage(), e);
            vo = null;
        }
        return vo;
    }
   
}
