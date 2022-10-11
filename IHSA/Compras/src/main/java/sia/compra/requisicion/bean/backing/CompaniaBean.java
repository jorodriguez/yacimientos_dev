/*
 * CompañiaBean.java
 * Creado el 23/06/2009, 11:36:26 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.Compania;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 23/06/2009
 */

@Named (value= CompaniaBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class CompaniaBean {

    //------------------------------------------------------
    public static final String BEAN_NAME = "companiaBean";
    //------------------------------------------------------

    
    @Inject
    private CompaniaImpl companiaServicioRemoto;

    /** Creates a new instance of CompaniaBean */
    public CompaniaBean() {
    }

    public Compania buscarPorNombre(Object nombreCompania) {
        return companiaServicioRemoto.buscarPorNombre(nombreCompania);
    }

    public Compania getCompania() {
        return companiaServicioRemoto.buscarPorNombre("Iberoamericana de Hidrocarburos S.A. de C.V.");
    }

    /**
     * @return Lista de Compañias
     */
    public List getListaCompanias() {
        List resultList = new ArrayList();
        try {
            List<Compania> tempList = companiaServicioRemoto.findAll();
            for (Compania Lista : tempList) {
                //
                    SelectItem item = new SelectItem(Lista.getNombre());
                    resultList.add(item);
                //
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }
}
