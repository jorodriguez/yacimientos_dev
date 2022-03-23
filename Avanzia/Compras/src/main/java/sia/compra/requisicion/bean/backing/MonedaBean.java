/*
 * MonedaBean.java
 * Creado el 7/07/2009, 08:41:05 AM
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
import sia.modelo.Moneda;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 7/07/2009
 */
@Named (value= MonedaBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class MonedaBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "monedaBean";
    //------------------------------------------------------

    
    @Inject
    private MonedaImpl monedaServicioRemoto;

    /** Creates a new instance of MonedaBean */
    public MonedaBean() {
    }
    
    public Moneda buscarPorNombre(String nombreMoneda, String compania) {
        return monedaServicioRemoto.buscarPorNombre(nombreMoneda, compania);
    }
    
    public Moneda buscarPorID(int id) {
        return monedaServicioRemoto.find(id);
    }

   
    /**
     * @param idCampo
     * @return Lista de Monedas
     */
    public List getListaMonedas(int idCampo) {
        List resultList = new ArrayList();
        try {
            List<MonedaVO> tempList = monedaServicioRemoto.traerMonedaActiva(idCampo);
            for (MonedaVO moneda : tempList) {
                    SelectItem item = new SelectItem(moneda.getId(), moneda.getNombre());
                    resultList.add(item);
            }            
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
            resultList = new ArrayList();
        }
        return resultList;
    }
}
