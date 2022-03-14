/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.evaluacion.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvEvaluacionSeccion;
import sia.modelo.CvEvaluacionTemplateDet;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.PreguntaVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class CvEvaluacionTemplateDetImpl extends AbstractFacade<CvEvaluacionTemplateDet> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    public CvEvaluacionTemplateDetImpl() {
        super(CvEvaluacionTemplateDet.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    public List<PreguntaVo> traerPreguntas(int seccionID) {
        String sb = "";
        sb += "	select a.id, a.pregunta ";
        sb += "	from cv_evaluacion_template_det a ";
        sb += "	where a.cv_evaluacion_seccion = " + seccionID;
        sb += "	and a.eliminado = false ";

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<PreguntaVo> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castPregunta(ocObjects));
            }
        }
        return lc;
    }

    private PreguntaVo castPregunta(Object[] obj) {
        PreguntaVo vo = new PreguntaVo();
        vo.setPreguntaId((Integer) obj[0]);
        vo.setPregunta((String) obj[1]);
        return vo;
    }

    
    public void guardar(String sesion, int idSeccion, PreguntaVo aux) {
            CvEvaluacionTemplateDet nuevo = new CvEvaluacionTemplateDet();
            nuevo.setPregunta(aux.getPregunta());            
            nuevo.setCvEvaluacionSeccion(new CvEvaluacionSeccion(idSeccion));
            nuevo.setEliminado(false);
            nuevo.setGenero(new Usuario(sesion));
            nuevo.setFechaGenero(new Date());
            nuevo.setHoraGenero(new Date());
            
            this.create(nuevo);
    }
    
    
    public void modificar(String sesion, PreguntaVo aux) {
            CvEvaluacionTemplateDet nuevo = this.find(aux.getPreguntaId());
            if(nuevo != null){
                nuevo.setPregunta(aux.getPregunta());                                    
                nuevo.setModifico(new Usuario(sesion));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());            
                this.edit(nuevo);
            }
    }
    
    
    public void eliminar(String sesion, int aux) {
            CvEvaluacionTemplateDet nuevo = this.find(aux);
            if(nuevo != null){
                nuevo.setEliminado(true);                                    
                nuevo.setModifico(new Usuario(sesion));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());            
                this.edit(nuevo);
            }
    }
}
