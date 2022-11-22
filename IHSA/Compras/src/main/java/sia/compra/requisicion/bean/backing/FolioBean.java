/*
 * FolioBean.java
 * Creado el 8/07/2009, 09:14:15 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;


import java.io.Serializable;
import javax.faces.bean.CustomScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.servicios.sistema.impl.FolioImpl;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 8/07/2009
 */
@Named (value= FolioBean.BEAN_NAME)
@ViewScoped
public class FolioBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "folioBean";
    //------------------------------------------------------

    
    @Inject
    private FolioImpl folioServicioRemoto;

    /** Creates a new instance of FolioBean */
    public FolioBean() {
    }

    public int getFolio(Object nombreComprobante){
        return folioServicioRemoto.getFolio(nombreComprobante);
    }
    
    
    /**
     * Metodo utilizado para las OC/S y Requisiciones para recoger los consecutivos 
     * @param nombreComprobante
     * @param idApCampo
     * @return 
     */
    public String getFolio(Object nombreComprobante,Integer idApCampo){
        return folioServicioRemoto.getFolio(nombreComprobante, idApCampo);
    }

}
