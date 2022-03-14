/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.gr.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.GrTipoArchivo;
import sia.modelo.gr.vo.GrTipoArchivoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class GrTipoArchivoImpl  extends AbstractFacade<GrTipoArchivo> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
     public GrTipoArchivoImpl() {
        super(GrTipoArchivo.class);
    }

    
    
    public List<GrTipoArchivoVO> getGrTiposArch() {
        List<GrTipoArchivoVO> tipos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION ");
            sb.append(" FROM GR_TIPO_ARCHIVO ");
            sb.append(" where ELIMINADO = 'False' ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                tipos = new ArrayList<GrTipoArchivoVO>();
                for (Object[] objects : lo) {
                    tipos.add(castTipo(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            tipos = null;
        }
        return tipos;
    }
    
    
    public GrTipoArchivoVO getGrTipoArch(int id) {
        GrTipoArchivoVO tipo = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" SELECT ID,NOMBRE,DESCRIPCION ");
            sb.append(" FROM GR_TIPO_ARCHIVO ");
            sb.append(" where ");
            sb.append(" ID = ").append(id).append(" ");
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                tipo = castTipo(obj); 
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            tipo = null;
        }
        return tipo;
    }

    private GrTipoArchivoVO castTipo(Object[] obj) {
        GrTipoArchivoVO o = new GrTipoArchivoVO();
        o.setId((Integer) obj[0]);
        o.setNombre((String) obj[1]);
        o.setDescripcion((String) obj[2]);
        return o;
    }
}
