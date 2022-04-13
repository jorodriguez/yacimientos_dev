/*
 * TituloBean.java
 * Creado el 22/09/2009, 09:20:48 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.requisicion.bean.backing;


import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.ClasificacionServicio;
import sia.servicios.convenio.impl.ClasificacionServicioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 22/09/2009
 */
@Named (value= ClasificacionServicioBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ClasificacionServicioBean {
    
    //------------------------------------------------------
    public static final String BEAN_NAME = "clasificacionServicioBean";
    //------------------------------------------------------

    
    @Inject
    private ClasificacionServicioImpl clasificacionServicioRemoto;
    
    /** Creates a new instance of TituloBean */
    public ClasificacionServicioBean() {
    }

    /**
     * @return Lista de titulos
     */
    public List getPorProveedorActividad(Object nombreProveedor, Object nombreActividad) {
        List resultList = new ArrayList();
        try {
            List<ClasificacionServicio> tempList = clasificacionServicioRemoto.getPorProveedorActividad(nombreProveedor, nombreActividad);
            if (tempList.isEmpty()) {
                SelectItem item = new SelectItem("NINGÚN ELEMENTO ENCONTRADO...");
                resultList.add(item);
            } else {
                for (ClasificacionServicio Lista : tempList) {
                    SelectItem item = new SelectItem(Lista.getNombre());
                    resultList.add(item);
                }
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

}
