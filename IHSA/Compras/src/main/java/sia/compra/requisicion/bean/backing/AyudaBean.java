/*
 * AyudaBean.java
 * Creado el 13/07/2009, 05:37:06 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.SiAyuda;
import sia.servicios.sistema.impl.SiAyudaImpl;
import sia.util.UtilLog4j;


/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 13/07/2009
 */

@Named (value = "ayudaBean")
@CustomScoped(value = "#{window}")
public class AyudaBean {

    @Inject
    private SiAyudaImpl servicioAyuda;

    /** Creates a new instance of AyudaBean */
    public AyudaBean() {
    }

    /**
     * @return Lista de los temas de ayuda
     */
    public SiAyuda[] getTemasAyuda() {
        try {
            List<SiAyuda> tempList = servicioAyuda.findAll();
            return tempList.toArray(new SiAyuda[tempList.size()]);
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return new SiAyuda[0];
    }
}
