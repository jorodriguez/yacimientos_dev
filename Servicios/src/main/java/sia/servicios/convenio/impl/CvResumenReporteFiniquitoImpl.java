/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvResumenReporteFiniquito;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ResumenHistoricoAvanceFiniquitoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class CvResumenReporteFiniquitoImpl extends AbstractFacade<CvResumenReporteFiniquito> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvResumenReporteFiniquitoImpl() {
        super(CvResumenReporteFiniquito.class);
    }

    
    public void guardar(String sesion, CvResumenReporteFiniquito crrf) {
        crrf.setDiferencia(crrf.getAvance() - traerUltimoAvance(crrf.getApCampo().getId()).doubleValue());
        crrf.setGenero(new Usuario(sesion));
        crrf.setFechaGenero(new Date());
        crrf.setHoraGenero(new Date());
        crrf.setEliminado(Boolean.FALSE);
        //
        create(crrf);
    }

    
    public List<CvResumenReporteFiniquito> traerTodos(int campoId) {
        if (campoId == 0) {
            return em.createNamedQuery("CvResumenReporteFiniquito.findAll").getResultList();
        } else {
            return em.createNamedQuery("CvResumenReporteFiniquito.traerPorCampo").setParameter("campo", campoId).getResultList();
        }
    }

    private BigDecimal traerUltimoAvance(int campoId) {
        try {
            String c = "select cf.avance from cv_resumen_reporte_finiquito cf "
                    + " where cf.ap_campo = " + campoId
                    + " and  cf.eliminado  = false "
                    + " order  by cf.id desc  limit 1";
            return (BigDecimal) em.createNativeQuery(c).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return BigDecimal.ZERO;
        }
    }

    
    public List<ResumenHistoricoAvanceFiniquitoVo> traerResumenGlobal() {
        String c = "select ac.nombre, crrf.fecha_genero , crrf.avance, crrf.total_contabilizado"
                + " from cv_resumen_reporte_finiquito crrf \n"
                + "	inner join ap_campo ac on crrf.ap_campo  = ac.id \n"
                + " where crrf.eliminado  = false \n"
                + " and crrf.id in (SELECT MAX(cr.id) FROM cv_resumen_reporte_finiquito cr GROUP BY cr.ap_campo) \n"
                + " ORDER BY crrf.id desc";
        try {
            List<ResumenHistoricoAvanceFiniquitoVo> res = new ArrayList<ResumenHistoricoAvanceFiniquitoVo>();
            List<Object[]> objs = em.createNativeQuery(c).getResultList();
            for (Object[] obj : objs) {
                ResumenHistoricoAvanceFiniquitoVo rchVo = new ResumenHistoricoAvanceFiniquitoVo();
                rchVo.setCampo((String) obj[0]);
                rchVo.setFechaGenero((Date) obj[1]);
                rchVo.setAvance((BigDecimal) obj[2] + " %");
                rchVo.setTotalContabilizado(((BigDecimal) obj[3]).intValue() );
                res.add(rchVo);
            }
            return res;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            UtilLog4j.log.error(e);
            return null;
        }
    }
}
