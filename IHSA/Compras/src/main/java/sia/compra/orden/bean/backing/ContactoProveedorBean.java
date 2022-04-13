/*
 * ContactoProveedorBean.java
 * Creado el 16/10/2009, 11:34:36 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.ContactoProveedor;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.util.UtilLog4j;


/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 16/10/2009
 */
@Named (value= ContactoProveedorBean.BEAN_NAME)
@ViewScoped
public class ContactoProveedorBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "contactoProveedorBean";
    //------------------------------------------------------
    
    @Inject
    private ContactoProveedorImpl contactoProveedorServicioRemoto;


    /** Creates a new instance of ContactoProveedorBean */
    public ContactoProveedorBean() {
    }

    /**
     * @return Lista de Contactos
     */
    public List getContactosPorProveedor(Object nombreProveedor) {
        List resultList = new ArrayList();
        try {
            List<ContactoProveedor> tempList =this.contactoProveedorServicioRemoto.getPorProveedor(nombreProveedor);
            if (tempList.isEmpty()) {
                SelectItem item = new SelectItem("- - - - - - - - - - - - - -");
                resultList.add(item);
            } else {
                for (ContactoProveedor Lista : tempList) {
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

    public void crearContacto(ContactoProveedor contactoProveedor){
        this.contactoProveedorServicioRemoto.create(contactoProveedor);
    }

}
