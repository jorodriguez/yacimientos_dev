/*
 * TipoObraBean.java
 * Creado el 7/07/2009, 08:42:51 AM
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
import sia.modelo.TipoObra;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.modelo.vo.RelProyectoTipoObraVO;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.TipoObraImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Named (value = TipoObraBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class TipoObraBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "tipoObraBean";
    //------------------------------------------------------
    @Inject
    private TipoObraImpl tipoObraServicioRemoto;
    @Inject
    private OcTareaImpl ocTareaImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;

    /**
     * Creates a new instance of TipoObraBean
     */
    public TipoObraBean() {
    }

    public TipoObra buscarPorNombre(Object nombreTipoObra) {
        return tipoObraServicioRemoto.buscarPorNombre(nombreTipoObra);
    }

    /**
     * @return Lista de Tipos de Obra
     */
    public List getListaTiposObra() {
        List resultList = new ArrayList();
        try {
            List<TipoObra> tempList = tipoObraServicioRemoto.findAll();
            for (TipoObra Lista : tempList) {
                SelectItem item = new SelectItem(Lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.info(this, ex.getMessage());
        }
        return resultList;
    }

//////////    public List getListaTiposObra(Object nombreProyectoOt) {
//////////        List resultList = new ArrayList();
//////////        SelectItem itemSinNada = new SelectItem("");
//////////        
//////////        resultList.add(itemSinNada);
//////////        try {
//////////            List<TipoObra> tempList = tipoObraServicioRemoto.getPorProyectoOt(nombreProyectoOt);
//////////            for (TipoObra Lista : tempList) {
//////////                    SelectItem item = new SelectItem(Lista.getNombre());
//////////                    resultList.add(item);
//////////            }
//////////            return resultList;
//////////        } catch (RuntimeException ex) {
//////////            UtilLog4j.log.info(this, ex.getMessage());
//////////        }
//////////        return resultList;
//////////    }
    public List getListaTiposObra(int idProyectoOt) {
        List resultList = new ArrayList();
        SelectItem itemSinNada = new SelectItem("");

        resultList.add(itemSinNada);
        try {
            List<RelProyectoTipoObraVO> tempList = tipoObraServicioRemoto.trarTipoObraPorProyectoOTid(idProyectoOt);
            for (RelProyectoTipoObraVO Lista : tempList) {
                SelectItem item = new SelectItem(Lista.getNombreTipoObra());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.info(this, ex.getMessage());
        }
        return resultList;
    }

    public List getListaTiposObraCompleta(Object nombreProyectoOt) {
        List resultList = new ArrayList();
        try {
            List<TipoObra> tempList = tipoObraServicioRemoto.getPorProyectoOt(nombreProyectoOt);
            for (TipoObra Lista : tempList) {
                SelectItem item = new SelectItem(Lista.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.info(this, ex.getMessage());
        }
        return resultList;
    }

   public List<SelectItem> listaTarea(int idProyectoOT, int idGerencia, int idUnidadCosto) {
         UtilLog4j.log.info(this, "idGerencia " + idGerencia);
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
            List<OcTareaVo> tempList = ocTareaImpl.traerNombrePorProyectoOtGerenciaUnidadCosto(idGer, idProyectoOT, idUnidadCosto);
            for (OcTareaVo lista : tempList) {
                SelectItem item = new SelectItem(lista.getIdTarea(), lista.getNombreTarea());
                resultList.add(item);
            }            
        } catch (RuntimeException ex) {
            UtilLog4j.log.info(this, ex.getMessage());
            resultList = new ArrayList<>();
        }
        return resultList;
    }
}
