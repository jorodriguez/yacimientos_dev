/*
 * ProyectoOtBean.java
 * Creado el 7/07/2009, 08:42:40 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.ProyectoOt;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.requisicion.impl.OcActividadPetroleraImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Named (value = ProyectoOtBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ProyectoOtBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "proyectoOtBean";
    //------------------------------------------------------
    @Inject
    private ProyectoOtImpl proyectoOtServicioRemoto;
    @Inject
    private OcTareaImpl ocTareaImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private OcActividadPetroleraImpl ocActividadPetroleraImpl;

    /**
     * Creates a new instance of ProyectoOtBean
     */
    public ProyectoOtBean() {
    }

    public ProyectoOt buscarPorNombre(Object nombreProyectoOt, Object nombreCompañia) {
        return proyectoOtServicioRemoto.buscarPorNombre(nombreProyectoOt, nombreCompañia);
    }

    public ProyectoOt buscarPorId(int idProyecto) {
        return proyectoOtServicioRemoto.find(idProyecto);
    }

    /**
     * @return Lista de Proyectos Ot
     */
    public List getListaPorCompania(Object rfc) {
        List resultList = new ArrayList<>();
        try {
            List<ProyectoOt> tempList = proyectoOtServicioRemoto.getPorCompania(rfc);
            for (ProyectoOt Lista : tempList) {
                SelectItem item = new SelectItem(Lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" +ex.getMessage());
        }
        return resultList;
    }

    //*lista de gerencias por ApCampo
    public List<SelectItem> getListaPorGerencia(int idGerencia, Object nombreCompania, Integer idApCampo) {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            List<ProyectoOtVo> tempList = proyectoOtServicioRemoto.getProyectoPorGerencia(idGerencia, nombreCompania, idApCampo);
            for (ProyectoOtVo lista : tempList) {
                SelectItem item = new SelectItem(lista.getId(), lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" +ex.getMessage());
        }
        return resultList;
    }

    public List<SelectItem> listaUnidadCosto(int idGerencia, int idProyectoOt, int idActividad) {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            int idGer;
            GerenciaVo g = gerenciaImpl.buscarPorId(idGerencia);
            if (g.getAbrev().contains(";")) {
                String[] cad = g.getAbrev().split(";");
                UtilLog4j.log.debug(this, "Gerencia: " + g.getNombre() + "Cadena :   " + cad[0]);
                GerenciaVo ga = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
                idGer = ga.getId();
            } else {
                idGer = g.getId();
            }
            List<OcTareaVo> tempList = ocTareaImpl.traerUnidadCostoPorGerenciaProyectoOT(idGer, idProyectoOt, idActividad);
            for (OcTareaVo lista : tempList) {
                SelectItem item = new SelectItem(lista.getIdUnidadCosto(), lista.getUnidadCosto());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" +ex.getMessage());
        }
        return resultList;
    }

    public List getListaPorGerenciaCompleta(int idGerencia, Object nombreCompania, Integer idApCampo) {
        UtilLog4j.log.info(this, "ApCampo " + idApCampo);
        List resultList = new ArrayList();
        try { //cambio de list.id por lis.nombre
            List<ProyectoOtVo> tempList = proyectoOtServicioRemoto.getProyectoPorGerencia(idGerencia, nombreCompania, idApCampo);
            for (ProyectoOtVo lista : tempList) {
                SelectItem item = new SelectItem(lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" +ex.getMessage());
        }
        return resultList;
    }

    public String traerProyectoOTJson(String rfc) {
        return proyectoOtServicioRemoto.traerProyectoOTJson(rfc);
    }

    private boolean contieneGerencia = false;
    
    private boolean contineOt = false;
    private int proyectoOtID = 0;
    public List<SelectItem> listaProyectoPorGerencia(int idGerencia, int idCampo, int proyectoOTID) {
        this.proyectoOtID = proyectoOTID;
        return listaProyectoPorGerencia(idGerencia, idCampo);
    }
    
    public List<SelectItem> listaProyectoPorGerencia(int idGerencia, int idCampo) {
        UtilLog4j.log.info(this, "ApCampo " + idCampo);
        List<SelectItem> resultList = new ArrayList<>();
        try {
            int idGer;
            GerenciaVo g = gerenciaImpl.buscarPorId(idGerencia);
            if (g.getAbrev().contains(";")) {
                String[] cad = g.getAbrev().split(";");
                UtilLog4j.log.debug(this, "Gerencia: " + g.getNombre() + "Cadena :   " + cad[0]);
                GerenciaVo ga = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
                idGer = ga.getId();
            } else {
                idGer = g.getId();
            }
            List<ProyectoOtVo> tempList = ocTareaImpl.traerProyectoOtPorGerencia(idGer, idCampo);
            for (ProyectoOtVo lista : tempList) {
                if(!this.isContineOt() && lista.getId() == this.proyectoOtID){
                    this.setContineOt(true);
                }
                SelectItem item = new SelectItem(lista.getId(), lista.getNombre());
                resultList.add(item);
            }            
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" +ex.getMessage());
            resultList = new ArrayList<>();
        }
        return resultList;
    }

    public OcTareaVo buscarTareaVo(int idGerencia, int idProyectoOt, int idUnidadCosto, int idNombreTarea) {
        int idGer;
        GerenciaVo g = gerenciaImpl.buscarPorId(idGerencia);
        if (g.getAbrev().contains(";")) {
            String[] cad = g.getAbrev().split(";");
            UtilLog4j.log.debug(this, "Gerencia: " + g.getNombre() + "Cadena :   " + cad[0]);
            GerenciaVo ga = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
            idGer = ga.getId();
        } else {
            idGer = g.getId();
        }
        return ocTareaImpl.traerTarea(idGer, idProyectoOt, idUnidadCosto, idNombreTarea, 0);
    }

    /**
     * @return the contieneGerencia
     */
    public boolean isContieneGerencia() {
        return contieneGerencia;
    }

    /**
     * @param contieneGerencia the contieneGerencia to set
     */
    public void setContieneGerencia(boolean contieneGerencia) {
        this.contieneGerencia = contieneGerencia;
    }

    /**
     * @return the contineOt
     */
    public boolean isContineOt() {
        return contineOt;
    }

    /**
     * @param contineOt the contineOt to set
     */
    public void setContineOt(boolean contineOt) {
        this.contineOt = contineOt;
    }
    
    public List<SelectItem> getListaActividades() {
        List<SelectItem> resultList = null;
        try {
            resultList = ocActividadPetroleraImpl.getActividadesItems();            
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" +ex.getMessage());
            resultList = new ArrayList<>();
        }
        return resultList;
    }
}
