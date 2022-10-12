/*
 * ServiciosAdicionalesBean.java
 * Creado el 10/09/2009, 01:58:05 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.catalogos.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.ServicioAdicional;
import sia.servicios.almacen.impl.ServicioAdicionalImpl;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 10/09/2009
 */
@Named (value="servicioAdicionalBean")
@SessionScoped
public class ServicioAdicionalBean implements Serializable{
@Inject
private ServicioAdicionalImpl serviciosAdicionalesServicioRemoto;
    /** Creates a new instance of ServiciosAdicionalesBean */
    public ServicioAdicionalBean() {
    }

 public List<ServicioAdicional> getPorServicioPrincipal(Object idServicioPrincipal) {

    return serviciosAdicionalesServicioRemoto.getPorServicioPrincipal(idServicioPrincipal);
 }

}
