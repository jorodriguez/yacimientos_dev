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
import sia.modelo.CvEvaluacionTemplate;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.SeccionVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class CvEvaluacionSeccionImpl extends AbstractFacade<CvEvaluacionSeccion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    public CvEvaluacionSeccionImpl() {
        super(CvEvaluacionSeccion.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    public List<SeccionVo> traerSecciones(int templateID) {

        String sb = "";
        sb += "	select a.id, a.nombre, a.maximo ";
        sb += "	from cv_evaluacion_seccion a ";
        sb += "	where a.cv_evaluacion_template = " + templateID;
        sb += "	and a.eliminado = false ";

        List<Object[]> lo = em.createNativeQuery(sb).getResultList();
        List<SeccionVo> lc = null;
        if (lo != null) {
            lc = new ArrayList<>();
            for (Object[] ocObjects : lo) {
                lc.add(castTemplate(ocObjects));
            }
        }
        return lc;
    }

    private SeccionVo castTemplate(Object[] obj) {
        SeccionVo vo = new SeccionVo();
        vo.setSeccionId((Integer) obj[0]);
        vo.setSeccionNombre((String) obj[1]);
        vo.setSeccionMaximo((Integer) obj[2]);
        return vo;
    }

    
    public void guardar(String sesion, int idTemp, SeccionVo aux) {
            CvEvaluacionSeccion nuevo = new CvEvaluacionSeccion();
            nuevo.setNombre(aux.getSeccionNombre());
            nuevo.setDescripcion(aux.getSeccionNombre());
            nuevo.setMaximo(aux.getSeccionMaximo());
            nuevo.setCvEvaluacionTemplate(new CvEvaluacionTemplate(idTemp));
            nuevo.setEliminado(false);
            nuevo.setGenero(new Usuario(sesion));
            nuevo.setFechaGenero(new Date());
            nuevo.setHoraGenero(new Date());
            
            this.create(nuevo);
    }
    
    
    public void modificar(String sesion, SeccionVo aux) {
            CvEvaluacionSeccion nuevo = this.find(aux.getSeccionId());
            if(nuevo != null){
                nuevo.setNombre(aux.getSeccionNombre());
                nuevo.setDescripcion(aux.getSeccionNombre());
                nuevo.setMaximo(aux.getSeccionMaximo());

                nuevo.setModifico(new Usuario(sesion));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());

                this.edit(nuevo);
            }
    }
    
    
    public void eliminar(String sesion, int aux) {
            CvEvaluacionSeccion nuevo = this.find(aux);
            if(nuevo != null){
                nuevo.setEliminado(true);
                nuevo.setModifico(new Usuario(sesion));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());
                this.edit(nuevo);
            }
    }
}
