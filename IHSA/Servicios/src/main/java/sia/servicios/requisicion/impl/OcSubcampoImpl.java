/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcSubcampo;
import sia.modelo.requisicion.vo.OcCodigoSubTareaVO;
import sia.modelo.requisicion.vo.OcSubCampoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Stateless 
public class OcSubcampoImpl extends AbstractFacade<OcSubcampo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcSubcampoImpl() {
        super(OcSubcampo.class);
    }
    
    
    public List<OcSubCampoVO> getSubcampos() {
        UtilLog4j.log.info(this, "#getCodigosSubtareas ");
        ArrayList<OcSubCampoVO> lst = new ArrayList<>();
        try {
            String consulta = " select id, codigo, nombre "
                    + " from oc_subcampo    "
                    + " where eliminado =  false  "
                    + " order by codigo ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();
            OcCodigoSubTareaVO vo = null;
            if (lo != null) {
                for (Object[] objects : lo) {
                    lst.add(new OcSubCampoVO((int) objects[0], (String) objects[1], (String) objects[2]));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde de las subtareas" + e.getMessage(), e);
            lst = new ArrayList<>();
        }
        return lst;
    }

    
    public OcSubCampoVO getSubCampo(int idSubTarea) {
        UtilLog4j.log.info(this, "#getCodigoSubtarea ");
        OcSubCampoVO vo = null;
        try {
            String consulta = " select id, codigo, nombre "
                    + " from oc_subcampo    "
                    + " where eliminado =  false and id = " + idSubTarea
                    + " order by codigo ";

            UtilLog4j.log.info(this, "query" + consulta);

            List<Object[]> lo = em.createNativeQuery(consulta).getResultList();

            if (lo != null && lo.size() > 0) {
                Object[] objects = lo.get(0);
                vo = new OcSubCampoVO((int) objects[0], (String) objects[1], (String) objects[2]);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener el detallde de la subtarea" + e.getMessage(), e);
            vo = null;
        }
        return vo;
    }
 
}
