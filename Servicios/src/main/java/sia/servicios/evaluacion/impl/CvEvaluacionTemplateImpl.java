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
import sia.modelo.Compania;
import sia.modelo.CvClasificacion;
import sia.modelo.CvEvaluacionTemplate;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.EvaluacionTemplateVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class CvEvaluacionTemplateImpl extends AbstractFacade<CvEvaluacionTemplate> {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    public CvEvaluacionTemplateImpl() {
        super(CvEvaluacionTemplate.class);
    }

   
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
   
    
    public List<EvaluacionTemplateVo> traerTemplatePorTipo(int clasificacionID, String compania) {

        String sb = "";
        sb += "	select a.id, a.nombre, a.descripcion, a.titulo, a.interpretacion, a.notas, c.nombre, c.id ";
        sb += "	from cv_evaluacion_template a ";
        sb += "	inner join cv_clasificacion c on c.id = a.cv_clasificacion ";
        sb += "	where a.eliminado = false ";     
        if(clasificacionID > 0){
            sb += "	and a.cv_clasificacion in (select coalesce(b.id, a.id, 0) from cv_clasificacion a left join cv_clasificacion b on b.id = a.cv_clasificacion where a.id = " + clasificacionID + ") ";
        }
        sb += "	and a.compania = '" + compania +"' ";
        sb += "	order by a.cv_clasificacion ";
        
        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<EvaluacionTemplateVo> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castTemplate(ocObjects));
            }
        }
        return lc;
    }
  
    private EvaluacionTemplateVo castTemplate(Object[] obj) {
        EvaluacionTemplateVo vo = new EvaluacionTemplateVo();
        vo.setId((Integer) obj[0]);        
        vo.setNombre((String) obj[1]);
        vo.setDescripcion((String) obj[2]);
        vo.setTitulo((String) obj[3]);
        vo.setInterpretacion((String) obj[4]);
        vo.setNotas((String) obj[5]);        
        vo.setClasificacion((String) obj[6]);        
        vo.setIdClasificacion((Integer) obj[7]);        
        return vo;
    }
    
    
    public void guardar(String sesion, String compania, EvaluacionTemplateVo aux) {
            CvEvaluacionTemplate nuevo = new CvEvaluacionTemplate();
            nuevo.setNombre(aux.getNombre());
            nuevo.setDescripcion(aux.getDescripcion());
            nuevo.setTitulo(aux.getTitulo());
            nuevo.setEliminado(false);
            nuevo.setCompania(new Compania(compania));
            nuevo.setCvClasificacion(new CvClasificacion(aux.getIdClasificacion()));
            
            nuevo.setGenero(new Usuario(sesion));
            nuevo.setFechaGenero(new Date());
            nuevo.setHoraGenero(new Date());
            
            this.create(nuevo);
    }
    
    
    public void modificar(String sesion, EvaluacionTemplateVo aux) {
            CvEvaluacionTemplate nuevo = this.find(aux.getId());
            if(nuevo != null){
                nuevo.setNombre(aux.getNombre());
                nuevo.setDescripcion(aux.getDescripcion());
                nuevo.setTitulo(aux.getTitulo());            
                nuevo.setCvClasificacion(new CvClasificacion(aux.getIdClasificacion()));
                nuevo.setModifico(new Usuario(sesion));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());

                this.edit(nuevo);
            }
    }
    
    
    public void eliminar(String sesion, int aux) {
            CvEvaluacionTemplate nuevo = this.find(aux);
            if(nuevo != null){
                nuevo.setEliminado(true);
                nuevo.setModifico(new Usuario(sesion));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());
                this.edit(nuevo);
            }
    }
}
