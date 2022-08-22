/*
 * correoBean.java
 * Creado el 2/07/2009, 07:46:32 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import javax.faces.bean.CustomScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.notificaciones.sistema.impl.CorreoImpl;


/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 2/07/2009
 */
@Named (value= CorreoBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class CorreoBean {

    //------------------------------------------------------
    public static final String BEAN_NAME = "correoBean";
    //------------------------------------------------------

    
    @Inject
    private CorreoImpl correoServicioRemoto;
    
    /** Creates a new instance of correoBean */
    public CorreoBean() {
    }

}
