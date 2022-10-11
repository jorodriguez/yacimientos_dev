/*
 * ProveedorBean.java
 * Creado el 24/08/2009, 05:45:35 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.modelo.Proveedor;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.util.ProveedorEnum;

/**
 *
 * @version 1.0
 * @date 24/08/2009
 */
@Named (value = ProveedorBean.BEAN_NAME)
@ViewScoped
//@CustomScoped(value = "#{window}")
public class ProveedorBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "proveedorBean";
    //------------------------------------------------------
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    private ContactoProveedorImpl contactoProveedorImpl;

    String jsonProveedores;

    /**
     * Creates a new instance of ProveedorBean
     */
    public ProveedorBean() {

    }

    public Proveedor getPorNombre(String nombreProveedor, String empresa) {
        return this.proveedorServicioRemoto.getPorNombre(nombreProveedor, empresa);
    }

    public void llenarJson(String empresa) {
        this.jsonProveedores = this.proveedorServicioRemoto.getProveedorJson(empresa, ProveedorEnum.ACTIVO.getId());
        PrimeFaces.current().executeScript( ";setJson(" + jsonProveedores + ");");
    }

    public void llenarJsonProveedor(int idProveedor, String empresa) {
        String datos = this.proveedorServicioRemoto.traerDatosProveedor(idProveedor, empresa);
        PrimeFaces.current().executeScript( ";datosProveedor('form1'," + datos + ");");
    }

    public List<ContactoProveedorVO> traerContactoPorProveedor(int idProveedor, int tipo) {
        return contactoProveedorImpl.traerContactoPorProveedor(idProveedor, tipo);
    }

    public String traerProveedorJson(String rfc) {
        return proveedorServicioRemoto.getProveedorJson(rfc, ProveedorEnum.ACTIVO.getId());
    }

}
