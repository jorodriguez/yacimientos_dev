/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.condicionPago.bean.model;

import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.modelo.orden.vo.ProveedorConPagoVo;
import sia.servicios.orden.impl.OcProveedorConPagoImpl;
import sia.sistema.bean.backing.Sesion;
 

/**
 *
 * @author mluis
 */
@ManagedBean
@CustomScoped(value = "#{window}")
public class RelacionConPagoProModel {

    //Servicios   
    @EJB
    private OcProveedorConPagoImpl ocProveedorConPagoImpl;
       
    //ManagedBeans    
    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;  
          
    //Vo
    private ProveedorConPagoVo proveedorConPagoVo;
            
    //Colecciones    
    private DataModel listData;     
    
    //Clases
    private String nomConPago = null;  
    
    /** Creates a new instance of CondicionPagoModel */
    public RelacionConPagoProModel() {       
    
    }
    
    
    
     /* Recupera las condiciones de pago de un proveedor */
    public DataModel getAllProveedorConPago() {             
        this.setListData(new ListDataModel(this.getOcProveedorConPagoImpl().findByNombreConPago(getNomConPago())));                              
        return getListData();        
    }
        
    public void eliminarRelacion() {                    
        this.ocProveedorConPagoImpl.eliminarProveedorConPago(this.getProveedorConPagoVo(), getSesion().getUsuario());    
        this.getAllProveedorConPago();
    }
       
    public OcProveedorConPagoImpl getOcProveedorConPagoImpl() {
        return ocProveedorConPagoImpl;
    }

    public void setOcProveedorConPagoImpl(OcProveedorConPagoImpl ocProveedorConPagoImpl) {
        this.ocProveedorConPagoImpl = ocProveedorConPagoImpl;
    }
    
    public String getNomConPago() {        
        return nomConPago;
    }
    
    public void setNomConPago(String nomConPago) {
        this.nomConPago = nomConPago;
    }
    
    public ProveedorConPagoVo getProveedorConPagoVo() {
        return proveedorConPagoVo;
    }
    
    public void setProveedorConPagoVo(ProveedorConPagoVo proveedorConPagoVo) {
        this.proveedorConPagoVo = proveedorConPagoVo;
    }    
    
    public DataModel getListData() {
        return listData;
    }

    public void setListData(DataModel listData) {
        this.listData = listData;
    }
    
    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }
}
 