/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.backing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ActionListener;
import javax.inject.Inject;




import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.PrimeFacesContext;
import sia.constantes.Constantes;
import sia.modelo.requisicion.vo.RequisicionView;
import sia.servicios.requisicion.impl.RequisicionImpl;
import static sia.constantes.Constantes.RUTA_COMPRAS_DESDE_REQ;
import static sia.constantes.Constantes.RUTA_SGL_MODULO;
import sia.modelo.orden.vo.OrdenView;
import sia.modelo.sgl.vo.AccesosDirectosView;
import sia.modelo.sgl.vo.SolicitudViajeView;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.sistema.bean.support.FacesUtils;
import sia.sistema.bean.support.PrimeUtils;

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
    
    @Inject
    private SgSolicitudViajeImpl solicitudViajeImpl;
    
    @Getter
    private List<RequisicionView> listaRequisiciones;
    
    @Getter
    private List<OrdenView> listaOrdenes;
    
    @Getter
    private List<SolicitudViajeView> listaSolicitudesViaje;
    
    @Getter
    private List<AccesosDirectosView> listaAccesos;
    
    @Getter @Setter
    private RequisicionView requisicion;
    
    @Getter @Setter
    private String consecutivo;
        
    @Getter @Setter
    private BusquedaEnum tipoBusqueda;
    
    enum BusquedaEnum {REQUISICION,ORDEN,PEDIDO,VIAJE};
            
    public PrincipalViewBean() { }

    @PostConstruct
    public void iniciar() {
        //loaders
        this.cargarListas();
        
    }

    private void cargarListas() {
        this.listaRequisiciones = requisicionImpl.getUltimasRequisicionesModificadas(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo());
        this.listaOrdenes = ordenImpl.getUltimasOrdenesModificadas(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo());
        //this.listaSolicitudesViaje = solicitudViajeImpl.getUltimasSolicitudesViaje(sesion.getUsuarioVo().getId(),sesion.getUsuarioVo().getIdCampo());
        this.cargarAccesosDirectos();
    }
    
    private void cargarAccesosDirectos(){
        this.listaAccesos = Arrays.asList(                
               AccesosDirectosView.builder()
                        .etiqueta("Crear una Requisición")
                        .icono("fa-plus").rutaModulo(getRutaModuloComprasDeRequ())
                        .rutaOpcion(getUrlSolicitarRequisicion())
                        .build(),                
                AccesosDirectosView.builder()
                        .etiqueta("Crear una solicitud de viaje")
                        .icono("fa-car-side").rutaModulo(RUTA_SGL_MODULO)
                        .rutaOpcion(getUrlCrearSolicitudViaje())
                        .build()
                
        );
    }
        
    public void buscar(ActionListener actionListener){
        
        if(this.consecutivo.isBlank() || this.consecutivo.isEmpty()){
            FacesUtils.addErrorMessage("Es requerido escribir el código");
            return;
        }
        
        tipoBusqueda = BusquedaEnum.REQUISICION;
        
        this.requisicion = requisicionImpl.buscarConsecutivo(this.consecutivo,sesion.getUsuarioVo().getId());                
                
        if(this.requisicion != null){            
            //PrimeUtils.executeScript("$('#modal_busqueda_requi').modal('show')");            
            return;            
        }
        
    }
        
     public void seleccionarRequisicion(ActionListener actionListener){
        
        
        String param = FacesUtils.getRequestParameter("indexRequisicion");
        
                        
        int paramInx = Integer.parseInt(param);
        
               
        this.requisicion = this.listaRequisiciones.get(paramInx);                              
    }
     

     public void seleccionarRequisicionRow(RequisicionView row){
        
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
    
    public String getUrlCrearSolicitudViaje(){
        return "/vistas/sgl/viaje/solicitud/solicitudViaje.xhtml";
    }
    
}
