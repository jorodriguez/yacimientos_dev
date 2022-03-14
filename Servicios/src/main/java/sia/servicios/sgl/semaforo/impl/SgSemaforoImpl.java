/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.semaforo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgSemaforo;
import sia.modelo.Usuario;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgSemaforoImpl extends AbstractFacade<SgSemaforo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgSemaforoImpl() {
        super(SgSemaforo.class);
    }
      
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
   
    public void limpiarQuery() {
        query.delete(0, query.length());
    }

    
    public List<SemaforoVo> traerSemaforo() {
        try {
            List<Object[]> list;
            limpiarQuery();
            query.append(" select se.ID, se.COLOR, se.descripcion, se.hora_minima, se.hora_maxima  ").append("  from SG_SEMAFORO se").append(" where se.eliminado =  'False'");
            list = em.createNativeQuery(query.toString()).getResultList();
            List<SemaforoVo> lv = new ArrayList<SemaforoVo>();
            for (Object[] objects : list) {
                lv.add(castEstadoVO(objects));
            }
            return lv;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
    
    
    public List<SelectItem> traerSemaforoItems() {
        List<SelectItem> lv = null;
        try {
            List<Object[]> list;
            limpiarQuery();
            query.append(" select se.ID, se.COLOR, se.descripcion, se.hora_minima, se.hora_maxima  ").append("  from SG_SEMAFORO se").append(" where se.eliminado =  'False'");
            list = em.createNativeQuery(query.toString()).getResultList();
            lv = new ArrayList<SelectItem>();
            for (Object[] objects : list) {
                lv.add(castSemaforoItem(objects));
            }            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
        return lv;
    }
    
    
    public SemaforoVo traerSemaforo(int id) {
        SemaforoVo v =  null;
        try {            
            limpiarQuery();
            query.append(" select se.ID, se.COLOR, se.descripcion, se.hora_minima, se.hora_maxima  ").append("  from SG_SEMAFORO se").append(" where se.id = ").append(id).append(" ");
            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();            
            if (obj != null) {                
                v = castEstadoVO(obj);
            }            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
        return v;
    }

    private SemaforoVo castEstadoVO(Object[] objects) {
        SemaforoVo semaforoVo = new SemaforoVo();
        try {
            int id = (Integer) objects[0];
            String estilo = "";
            semaforoVo.setIdSemaforo(id);
            semaforoVo.setColor((String) objects[1]);
            if (id == 1) {
                estilo = "verde";
            } else if (id == 2) {
                estilo = "amarillo";
            } else if (id == 3) {
                estilo = "rojo";
            } else if (id == 4) {
                estilo = "negro";
            }
            semaforoVo.setEstilo(estilo);
            semaforoVo.setDescripcion((String) objects[2]);
//            semaforoVo.setHoraMinima((Date) objects[3]);
//            semaforoVo.setHoraMaxima((Date) objects[4]);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return semaforoVo;
    }
    
    private SelectItem castSemaforoItem(Object[] objects) {
        SelectItem semaforoVo = new SelectItem();
        try {
            semaforoVo.setValue((Integer) objects[0]);
            semaforoVo.setLabel((String) objects[1]);            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return semaforoVo;
    }

    
    public void modificar(String idSesion, SemaforoVo semaforoVo) {
        SgSemaforo sgSemaforo = find(semaforoVo.getIdSemaforo());
        String ae = sgSemaforo.toString();
        sgSemaforo.setDescripcion(semaforoVo.getDescripcion());
//        sgSemaforo.setHoraMinima(semaforoVo.getHoraMinima());
//        sgSemaforo.setHoraMaxima(semaforoVo.getHoraMaxima());
        sgSemaforo.setModifico(new Usuario(idSesion));
        sgSemaforo.setHoraModifico(new Date());

        sgSemaforo.setFechaModifico(new Date());
        edit(sgSemaforo);
        //Actualiza el estado del semaforo
        if (semaforoVo.getIdSemaforo() == Constantes.ID_COLOR_SEMAFORO_AMARILLO || semaforoVo.getIdSemaforo() == Constantes.ID_COLOR_SEMAFORO_VERDE || semaforoVo.getIdSemaforo() == Constantes.ID_COLOR_SEMAFORO_ROJO) {
            List<SemaforoVo> lEsColor = sgEstadoSemaforoRemote.traerEstadoSemaforoPorColor(semaforoVo.getIdSemaforo(), Constantes.BOOLEAN_TRUE);
            for (SemaforoVo semVo : lEsColor) {
//                sgEstadoSemaforoRemote.actualizar(semVo.getIdEstadoSemaforo(), idSesion, semaforoVo.getHoraMinima(), semaforoVo.getHoraMaxima(), Constantes.NO_ELIMINADO);
            }
        }

    }
}
