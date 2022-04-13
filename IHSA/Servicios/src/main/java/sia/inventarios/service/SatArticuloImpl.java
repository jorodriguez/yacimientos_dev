/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SatArticulo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.SatArticuloVO;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Stateless 
public class SatArticuloImpl extends AbstractFacade<SatArticulo>  {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SatArticuloImpl() {
        super(SatArticulo.class);
    }

    
    public List<SelectItem> getSatArtsItems() {
        List<SelectItem> arts = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,CODIGO,DESCRIPCION ");
            sb.append(" FROM SAT_ARTICULO ");
            sb.append(" where ELIMINADO = 'False' ");            
            sb.append(" order by CODIGO ");
            
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                arts = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    arts.add(castMapaItem(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            arts = new ArrayList<SelectItem>();
        }
        return arts;
    }
    
    
    public List<SatArticuloVO> getSatArtsVO(String cadena) {
        List<SatArticuloVO> arts = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID, CODIGO, DESCRIPCION ");
            sb.append(" FROM SAT_ARTICULO ");
            sb.append(" where ELIMINADO = 'False' ");    
            if(cadena != null && !cadena.isEmpty()){
                sb.append(" and (upper(CODIGO) LIKE UPPER('%").append(cadena).append("%') OR upper(DESCRIPCION) LIKE UPPER('%").append(cadena).append("%') ) ");
            }
            sb.append(" order by DESCRIPCION limit 200 ");
            
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            SatArticuloVO vo = null;
            if (lo != null) {
                arts = new ArrayList<SatArticuloVO>();                
                for (Object[] objects : lo) {
                    vo = new SatArticuloVO();
                    vo.setId(objects[0] != null ? (Integer) objects[0] : 0);
                    vo.setCodigo((String) objects[1]);
                    vo.setDescripcion((String) objects[2]);
                    arts.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            arts = new ArrayList<SatArticuloVO>();
        }
        return arts;
    }
    
    private SelectItem castMapaItem(Object[] obj) {
        return new SelectItem((Integer) obj[0], new StringBuilder().append((String) obj[1]).append("-").append((String) obj[2]).toString());
    }
}
