/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcSubTarea;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Stateless 
public class OcSubTareaImpl extends AbstractFacade<OcSubTarea>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcSubTareaImpl() {
        super(OcSubTarea.class);
    }
    
    
    public List<SelectItem> traerLstCentoCostosItems(int idOcTarea, String nombre) {
        List<SelectItem> ltn = null;
        try {
            List<Object[]> obj = null;
            clearQuery();

            query.append(" select s.id, cst.CODIGO, cst.NOMBRE "
                        +" from OC_SUBTAREA s"
                        +" inner join OC_CODIGO_SUBTAREA cst on cst.id = s.OC_CODIGO_SUBTAREA"
                        +" where s.ELIMINADO = 'False' "
                        +" and s.OC_TAREA = ").append(idOcTarea);
            if(nombre != null && !nombre.isEmpty()){
                query.append(" and upper(s.NOMBRE) = upper('").append(nombre).append("') ");
            }

            UtilLog4j.log.info(this, "subTareas: " + query.toString());
            //
            obj = em.createNativeQuery(query.toString()).getResultList();
            if (obj != null) {
                ltn = new ArrayList<SelectItem>();
                SelectItem item = null;
                for (Object[] objects : obj) {
                    item = new SelectItem((Integer) objects[0], (String) objects[2]);
                    ltn.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio  un error al recuperar las subtareas a por tarea . " + e.getMessage());
            ltn = new ArrayList<SelectItem>();
        }
        return ltn;
    }
    
    
    public List<SelectItem> traerLstCentoCostosItemsCampoActividad(int idOcNombreT, int apCampoID, int actPetroleraID, String nombreSubTarea, int idProyOT) {
        List<SelectItem> ltn = null;
        try {
            List<Object[]> obj = null;
            clearQuery();
            if(nombreSubTarea != null && !nombreSubTarea.isEmpty()){
                query.append(" select st.ID as IDs, cst.CODIGO, cst.NOMBRE ");
            } else {
                query.append(" select -999 as IDs, cst.CODIGO, cst.NOMBRE ");
            }
            query.append(" from OC_TAREA t ");
            query.append(" inner join OC_SUBTAREA st on st.OC_TAREA = t.id and st.ELIMINADO = 'False' ");
            query.append(" inner join PROYECTO_OT ot on ot.id = t.PROYECTO_OT and ot.ELIMINADO = 'False' ");
            query.append(" inner join OC_CODIGO_SUBTAREA cst on cst.id = st.OC_CODIGO_SUBTAREA and cst.ELIMINADO = 'False' ");
            
            query.append(" where t.ELIMINADO = 'False' ");
            query.append(" and t.OC_NOMBRE_TAREA = ").append(idOcNombreT);
            query.append(" and ot.AP_CAMPO = ").append(apCampoID);
            query.append(" and t.OC_ACTIVIDADPETROLERA = ").append(actPetroleraID);
            
            if(nombreSubTarea != null && !nombreSubTarea.isEmpty()){
                query.append(" and upper(cst.NOMBRE) = upper('").append(nombreSubTarea).append("') ");
            }
            if(idProyOT > 0){
                query.append(" and ot.id = ").append(idProyOT);
            }
            query.append(" group by IDs, cst.CODIGO, cst.NOMBRE ");

            UtilLog4j.log.info(this, "subTareas: " + query.toString());
            //
            obj = em.createNativeQuery(query.toString()).getResultList();
            if (obj != null) {
                ltn = new ArrayList<SelectItem>();
                SelectItem item = null;
                for (Object[] objects : obj) {
                    item = new SelectItem((Integer) objects[0], (String) objects[2]);
                    ltn.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio  un error al recuperar las subtareas a por tarea . " + e.getMessage());
            ltn = new ArrayList<SelectItem>();
        }
        return ltn;
    }
    
    
    public int traerSubTareaID(int tareaID, int subTareaCodigoID) {
        int retValue = 0;
        try {            
            clearQuery();
            query.append(" select st.ID ");
            query.append(" from OC_SUBTAREA st ");            
            query.append(" where 1 = 1 ");
                        
            if(tareaID > 0){
                query.append(" and st.oc_tarea = ").append(tareaID);
            }
            
            if(subTareaCodigoID > 0){
                query.append(" and st.oc_codigo_subtarea = ").append(subTareaCodigoID);
            }
            
            query.append(" order by st.eliminado, st.id limit 1 ");
            
            UtilLog4j.log.info(this, "subTareas: " + query.toString());
            //
            Object obj = em.createNativeQuery(query.toString()).getSingleResult();
            if (obj != null) {
                retValue = (Integer) obj;
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio  un error al recuperar las subtareas a por tarea . " + e.getMessage());
            retValue = 0;
        }
        return retValue;
    }

}
