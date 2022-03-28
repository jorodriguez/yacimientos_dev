/*
 * GerenciaBean.java
 * Creado el 7/07/2009, 08:42:23 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
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
import sia.modelo.Gerencia;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.requisicion.impl.OcGerenciaProyectoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0 Modificado: Marino Luis 12/06/2014
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Named (value = GerenciaBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class GerenciaBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "gerenciaBean";
    //------------------------------------------------------
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private OcGerenciaProyectoImpl ocGerenciaProyectoImpl;

    /**
     * Creates a new instance of GerenciaBean
     */
    public GerenciaBean() {
    }

    public Gerencia findByNameAndCompania(String nombreGerencia, String rfcCompania) {
        Gerencia gerencia = this.gerenciaImpl.findByNameAndCompania(nombreGerencia, rfcCompania, false);
        return (gerencia != null ? gerencia : null);
    }

    public Gerencia findByIdAndCompania(int idGerencia, String rfcCompania) {
        Gerencia gerencia = this.gerenciaImpl.findByIdAndCompania(idGerencia, rfcCompania, false);
        return (gerencia != null ? gerencia : null);
    }

    public Gerencia buscarPorId(Object idGerencia) {
        return gerenciaImpl.find(idGerencia);
    }

    /**
     * @return Lista de Gerencias
     */
    //public List getListaGerencias(String rfcCompania, int idCampo) {
    public List<SelectItem> getListaGerencias(String rfcCompania, int idCampo) {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            List<GerenciaVo> tempList = gerenciaImpl.getAllGerenciaByApCompaniaAndApCampo(rfcCompania, idCampo, "nombre", true, true, false);
            for (GerenciaVo lista : tempList) {
                SelectItem item = new SelectItem(lista.getId(), lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    /**
     * @return Lista de Gerencias
     */
    //public List getListaGerencias(String rfcCompania, int idCampo) {
    
    public List<SelectItem> listaGerenciasConAbreviatura(int idCampo) {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            List<GerenciaVo> tempList = gerenciaImpl.traerGerenciaAbreviatura(idCampo);
            for (GerenciaVo lista : tempList) {    
                if(!this.contieneGerencia && this.gerenciaAux == lista.getId()){
                    this.contieneGerencia = true;
                }
                SelectItem item = new SelectItem(lista.getId(), lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    public List getListaGerenciasCompleta(String rfcCompania) {
        List resultList = new ArrayList();
        try {
            List<GerenciaVo> tempList = gerenciaImpl.getAllGerenciaByApCompania(rfcCompania, "nombre", true, false);
            for (GerenciaVo lista : tempList) {
                SelectItem item = new SelectItem(lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    private boolean contieneGerencia = false;
    private int gerenciaAux = 0;
    
    public List<SelectItem> traerGereciaActivoFijo(int idCampo, int gerenciaID) {
        this.gerenciaAux = gerenciaID;
        return this.traerGereciaActivoFijo(idCampo);        
    }
    
    public List<SelectItem> listaGerenciasConAbreviatura(int idCampo, int gerenciaID) {
        this.gerenciaAux = gerenciaID;
        return this.listaGerenciasConAbreviatura(idCampo);
    }
    
    public List<SelectItem> traerGereciaActivoFijo(int idCampo) {
        try {
            List<SelectItem> ls = new ArrayList<>();
            List<GerenciaVo> lg = ocGerenciaProyectoImpl.traerGerencia(idCampo);

            for (GerenciaVo gerenciaVo : lg) {
                if(!this.contieneGerencia && this.gerenciaAux == gerenciaVo.getId()){
                    this.contieneGerencia = true;
                }
                
                ls.add(new SelectItem(gerenciaVo.getId(), gerenciaVo.getNombre()));
            }
            return ls;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio Ocurrio un error al recuoerar las gerencias " + e.getMessage());
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuoerar las gerencias " + e.getMessage());
            return null;
        }
    }

    public List<SelectItem> traerProyectoActivoFijo(int idGerencia, int idCampo) {
        try {
            int idGer = 0;
            GerenciaVo g = gerenciaImpl.buscarPorId(idGerencia);
            if (g != null && g.getId() > 0) {
                idGer = g.getId();
            }
            List<SelectItem> ls = new ArrayList<>();
            for (ProyectoOtVo potVo : ocGerenciaProyectoImpl.traerProyectoOt(idGer, idCampo)) {
                ls.add(new SelectItem(potVo.getId(), potVo.getNombre()));
            }
            return ls;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuoerar las pryectos " + e.getMessage());
            return null;
        }
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
}
