/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ActionListener;
import javax.inject.Inject;




import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeFacesContext;
import sia.modelo.requisicion.vo.RequisicionView;
import sia.servicios.requisicion.impl.RequisicionImpl;
import static sia.constantes.Constantes.RUTA_COMPRAS_DESDE_REQ;
import sia.modelo.orden.vo.OrdenView;
import sia.servicios.orden.impl.OrdenImpl;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilSia;
/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class PrincipalViewBean implements Serializable {   

    @Inject
    private Sesion sesion;

    @Inject
    private RequisicionImpl requisicionImpl;
    
    @Inject
    private OrdenImpl ordenImpl;
    
    @Getter
    private List<RequisicionView> listaRequisiciones;
    
    @Getter
    private List<OrdenView> listaOrdenes;
    
    @Getter @Setter
    private RequisicionView requisicion;
    
            
    public PrincipalViewBean() { }

    @PostConstruct
    public void iniciar() {
        //loaders
        this.cargarListas();
        
    }

    private void cargarListas() {
        System.out.println("@cargarListas");
        this.listaRequisiciones = requisicionImpl.getUltimasRequisicionesModificadas(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo());
        this.listaOrdenes = ordenImpl.getUltimasOrdenesModificadas(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo());
        
    }
    
     public void seleccionarRequisicion(ActionListener actionListener){
         System.out.println("@seleccionarRequisicion");
        
        String param = FacesUtils.getRequestParameter("indexRequisicion");
        
         System.out.println("param index "+param);        
                        
        int paramInx = Integer.parseInt(param);
        
        System.out.println("param index INT "+paramInx);
               
        this.requisicion = this.listaRequisiciones.get(paramInx);                      
        
    }
     

     public void seleccionarRequisicionRow(RequisicionView row){
         System.out.println("@seleccionarRequisicionRow "+ (row == null));
        
        this.requisicion = row;
                         
    }
          
      
    public String getRutaModuloComprasDeRequ(){
        return RUTA_COMPRAS_DESDE_REQ;
    }
    
    public String getUrlSolicitarRequisicion(){
        return "/vistas/SiaWeb/Requisiciones/CrearRequisicion.xhtml";
    }
    
    public String getRutaModuloSolicitaOrdenDeRequ(){
        //falta
        return RUTA_COMPRAS_DESDE_REQ;
    }
    
    public String getUrlSolicitarOrden(){
        return "/vistas/SiaWeb/Requisiciones/CrearRequisicion.xhtml";
    }
    
}
