/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.evaluacion.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvHistorialExterno;
import sia.modelo.contrato.vo.EvaluacionRespuestaVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class CvHistorialExternoImpl extends AbstractFacade<CvHistorialExterno> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    public CvHistorialExternoImpl() {
        super(CvHistorialExterno.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    public List<EvaluacionRespuestaVo> traerEvaluacionRespuestas(int idApCampo, int idGerencia, int idProveedor, Date fechaI, Date fechaF, int tipoID) {
        List<EvaluacionRespuestaVo> lvo = null;
        String sb = "";
        sb += "	select a.id, a.contrato, a.gerencia_nom, a.proveedor_nom, a.fecha_eva,  ";        
        sb += "	a.observaciones, a.gerencia_realiza_nombre, a.correo_proveedor, ";
        sb += "	a.calidad_del_servicio, a.cumplimiento_de_plazo, a.cumplimiento_hse, a.servicio_diario, a.cumplimiento_de_cantidad,  ";
        sb += "	a.servicio_durante_y_postventa, a.seguridad_y_medio_ambiente, a.documentacion_final, a.servicio_durante_la_obra, a.puntaje_total  ";
        sb += "	from cv_historial_externo a ";
        sb += "	where a.eliminado =  false ";
        
        if (idApCampo > 0) {
            sb += "	and a.ap_campo =  " + idApCampo;
        }
        
        if (tipoID > 0) {
            sb += "	and a.cv_clasificacion =  " + tipoID;
        }

        if (idGerencia > 0) {
            sb += "	and a.gerencia =  " + idGerencia;
        }

        if (idProveedor > 0) {
            sb += "	and a.proveedor =  " + idProveedor;
        }

        if (fechaI != null && fechaF != null) {
            sb += " and a.fecha_eva between   ?  and ? ";
        }

        List<Object[]> lo = null;
        if (fechaI != null && fechaF != null) {
            lo = em.createNativeQuery(sb).setParameter(1, fechaI).setParameter(2, fechaF).getResultList();
        } else {
            lo = em.createNativeQuery(sb).getResultList();
        }

        if (lo != null) {
            lvo = new ArrayList<>();            
            EvaluacionRespuestaVo vo = null;
            for (Object[] obj : lo) {                
                vo = new EvaluacionRespuestaVo();
                vo.setIdEvaluacion((Integer) obj[0]);
                vo.setCodigoConvenio((String) obj[1]);                
                vo.setNombreGerencia((String) obj[2]);
                vo.setNombreProveedor((String) obj[3]);
                vo.setFecha((Date) obj[4]);                               
                vo.setObservaciones((String) obj[5]);
                vo.setNombreGerenciaRE((String) obj[6]);
                vo.setCorreo((String) obj[7]);

                vo.getCalidadDelServicio().setTotal(obj[8] != null ? ((BigDecimal) obj[8]).doubleValue() : 0.0);
                vo.getCumplimientoDePlazo().setTotal(obj[9] != null ? ((BigDecimal) obj[9]).doubleValue() : 0.0);
                vo.getCumplimientoHSE().setTotal(obj[10] != null ? ((BigDecimal) obj[10]).doubleValue() : 0.0);
                vo.getServicioDiario().setTotal(obj[11] != null ? ((BigDecimal) obj[11]).doubleValue() : 0.0);
                vo.getCumplimientoDeCantidad().setTotal(obj[12] != null ? ((BigDecimal) obj[12]).doubleValue() : 0.0);
                vo.getServicioDurantePostventa().setTotal(obj[13] != null ? ((BigDecimal) obj[13]).doubleValue() : 0.0);
                vo.getSeguridadMedioAmbiente().setTotal(obj[14] != null ? ((BigDecimal) obj[14]).doubleValue() : 0.0);
                vo.getDocumentacionFinal().setTotal(obj[15] != null ? ((BigDecimal) obj[15]).doubleValue() : 0.0);
                vo.getServicioDuranteObra().setTotal(obj[16] != null ? ((BigDecimal) obj[16]).doubleValue() : 0.0);                
                vo.setPuntosTotal(obj[17] != null ? ((BigDecimal) obj[17]).doubleValue() : 0.0);

                lvo.add(vo);
            }
        }
        return lvo;
    }
    
}
