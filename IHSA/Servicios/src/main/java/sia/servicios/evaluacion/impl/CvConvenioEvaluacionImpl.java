/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.evaluacion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvConvenioEvaluacion;
import sia.modelo.contrato.vo.ContratoEvaluacionVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author jcarranza
 */
@Stateless 
public class CvConvenioEvaluacionImpl  extends AbstractFacade<CvConvenioEvaluacion> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    public CvConvenioEvaluacionImpl() {
        super(CvConvenioEvaluacion.class);
    }

   
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    
    public List<ContratoEvaluacionVo> traerEvaluacionTemplate(int idConvenio){
        List<ContratoEvaluacionVo> lvo = null;        
	String sb = "";        
        sb += "	select c.id, co.id, c.cv_evaluacion_template, g.id, g.nombre, u.id, u.nombre, t.nombre, cl.nombre ";
        sb += "	from cv_convenio_evaluacion c ";
        sb += "	inner join gerencia g on g.id = c.gerencia and g.eliminado = false ";
        sb += "	inner join convenio co on co.id = c.convenio  and co.eliminado = false ";
        sb += "	inner join usuario u on u.id = c.responsable  ";
        sb += "	inner join cv_evaluacion_template t on t.id = c.cv_evaluacion_template ";
        sb += "	inner join cv_clasificacion cl on cl.id = t.cv_clasificacion ";
        sb += "	where c.eliminado = false ";
        sb += "	and c.convenio = " + idConvenio;
	
	List<Object[]> lo = em.createNativeQuery(sb).getResultList();

	if (lo != null) {
	    lvo = new ArrayList<ContratoEvaluacionVo>();
	    for (Object[] lo1 : lo) {
		lvo.add(castContratoEvaluacionVo(lo1));
	    }
	}
	return lvo;
    }
    
    
    public ContratoEvaluacionVo traerEvaluacionTemplateID(int idConvenioTemp){
        ContratoEvaluacionVo vo = null;        
	String sb = "";        
        sb += "	select c.id, co.id, c.cv_evaluacion_template, g.id, g.nombre, u.id, u.nombre, t.nombre, cl.nombre ";
        sb += "	from cv_convenio_evaluacion c ";
        sb += "	inner join gerencia g on g.id = c.gerencia and g.eliminado = false ";
        sb += "	inner join convenio co on co.id = c.convenio  and co.eliminado = false ";
        sb += "	inner join usuario u on u.id = c.responsable  ";
        sb += "	inner join cv_evaluacion_template t on t.id = c.cv_evaluacion_template ";
        sb += "	inner join cv_clasificacion cl on cl.id = t.cv_clasificacion ";
        sb += "	where c.eliminado = false ";
        sb += "	and c.id = " + idConvenioTemp;
	
	List<Object[]> lo = em.createNativeQuery(sb).getResultList();

	if (lo != null) {
	    vo = new ContratoEvaluacionVo();
	    for (Object[] lo1 : lo) {
		vo = castContratoEvaluacionVo(lo1);
	    }
	}
	return vo;
    }
    
    private ContratoEvaluacionVo castContratoEvaluacionVo(Object[] obj) {
	ContratoEvaluacionVo vo = new ContratoEvaluacionVo();
	vo.setId((Integer) obj[0]);
	vo.setIdConvenio((Integer) obj[1]);
        vo.setIdEvaTemp((Integer) obj[2]);
        vo.setIdGerencia((Integer) obj[3]);
	vo.setNombreGerencia((String) obj[4]);	
	vo.setUsuario((String) obj[5]);
        vo.setNombreUsuario((String) obj[6]);	
        vo.setNombreTemplate((String) obj[7]);	
        vo.setNombreTipo((String) obj[8]);	
	return vo;
    }
}
