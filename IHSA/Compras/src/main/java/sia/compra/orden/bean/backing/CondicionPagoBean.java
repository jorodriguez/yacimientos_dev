/*
 * CondicionPagoBean.java
 * Creado el 16/10/2009, 12:45:49 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.*;
import sia.modelo.orden.vo.ProveedorConPagoVo;
import sia.modelo.vo.GeneralVo;
import sia.util.UtilLog4j;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.servicios.catalogos.impl.CondicionPagoImpl;
import sia.servicios.orden.impl.OcProveedorConPagoImpl;
import sia.servicios.orden.impl.OcTerminoPagoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 16/10/2009
 */
@Named (value = CondicionPagoBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class CondicionPagoBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "condicionPagoBean";
    //------------------------------------------------------
    
    @Inject
    private UsuarioBean usuarioBean;
    @Inject
    private CondicionPagoImpl condicionPagoServicioRemoto;
    @Inject
    private OcProveedorConPagoImpl ocProveedorConPagoImpl;
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    private OcTerminoPagoImpl ocTerminoPagoImpl;
    //Entidades
    private OcProveedorConPago ocProveedorConPago;
    private CondicionPago condicionPago;
    private Proveedor proveedor;
    //Colecciones
    private List<String> listData;
//    @ManagedProperty(value = "#{sesion}")
//    private Sesion sesion;
    private String nombreProveedor;
    private int idProveedor;
    private String condicionSeleccionada;

    /**
     * Creates a new instance of CondicionPagoBean
     */
    public CondicionPagoBean() {
    }

    public void getAllConPago() {
        setListData(condicionPagoServicioRemoto.traerNombreConPagoQueryNativo());
    }

    public List getCondicionesPago() {
        List resultList = new ArrayList();
        try {
            List<ProveedorConPagoVo> tempList = this.getOcProveedorConPagoImpl().findByNombrePro(this.getNombreProveedor());
            if (tempList.isEmpty()) {
                SelectItem item = new SelectItem("- - - - - - - - - - - - - -");
                resultList.add(item);
            } else {
                for (ProveedorConPagoVo Lista : tempList) {
                    SelectItem item = new SelectItem(Lista.getNombreConPago());
                    resultList.add(item);
                }
            }

            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    public List traerCondicionesPago() {
        List resultList = new ArrayList();
        try {
            List<ProveedorConPagoVo> tempList = this.getOcProveedorConPagoImpl().traerCondicionPorIdProveedor(getIdProveedor(),
                    usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
            for (ProveedorConPagoVo Lista : tempList) {
                SelectItem item = new SelectItem(Lista.getNombreConPago());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    public List<SelectItem> traerTerminoPago() {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            List<GeneralVo> tempList = ocTerminoPagoImpl.listaTerminoPago(usuarioBean.getCompania().getRfc());
            for (GeneralVo gvo : tempList) {
                SelectItem item = new SelectItem(gvo.getValor(), gvo.getNombre());
                resultList.add(item);
            }
            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    public ProveedorConPagoVo buscarConPagoProPorNombre() {
        return this.getOcProveedorConPagoImpl().findByNombres(getNombreProveedor(), getCondicionSeleccionada(), usuarioBean.getCompania().getRfc());
    }

    public void guardarProveedorConPago(Usuario usuario) {
        this.buscarProveedorPorNombre();
        this.buscarConPagoPorNombre();
        this.getOcProveedorConPagoImpl().guardarProveedorConPago(this.getCondicionPago(), this.getProveedor().getId(), usuario, usuarioBean.getCompania().getRfc());

    }

    public void buscarProveedorPorNombre() {
        this.setProveedor(proveedorServicioRemoto.getPorNombre(getNombreProveedor(), usuarioBean.getCompania().getRfc()));
    }

    public void buscarConPagoPorNombre() {
        this.setCondicionPago(condicionPagoServicioRemoto.buscarPorNombre(getCondicionSeleccionada(), false));
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getCondicionSeleccionada() {
        return condicionSeleccionada;
    }

    public void setCondicionSeleccionada(String condicionSeleccionada) {
        this.condicionSeleccionada = condicionSeleccionada;
    }

    public CondicionPagoImpl getCondicionPagoServicioRemoto() {
        return condicionPagoServicioRemoto;
    }

    public void setCondicionPagoServicioRemoto(CondicionPagoImpl condicionPagoServicioRemoto) {
        this.condicionPagoServicioRemoto = condicionPagoServicioRemoto;
    }

    public OcProveedorConPagoImpl getOcProveedorConPagoImpl() {
        return ocProveedorConPagoImpl;
    }

    public void setOcProveedorConPagoImpl(OcProveedorConPagoImpl ocProveedorConPagoImpl) {
        this.ocProveedorConPagoImpl = ocProveedorConPagoImpl;
    }

    public OcProveedorConPago getOcProveedorConPago() {
        return ocProveedorConPago;
    }

    public void setOcProveedorConPago(OcProveedorConPago ocProveedorConPago) {
        this.ocProveedorConPago = ocProveedorConPago;
    }

    public CondicionPago getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(CondicionPago condicionPago) {
        this.condicionPago = condicionPago;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<String> getListData() {
        return listData;
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }
}
