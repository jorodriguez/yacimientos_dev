
package sia.controloficios.backing.bean;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sia.controloficios.sistema.soporte.FacesUtils;

/**
 * Bean para la pantalla de Detalle de Oficios.
 *
 * @author esapien
 */
//@ManagedBean
@Named(value = "oficioDetalleBeanTest")
@ViewScoped
public class OficioDetalleBeanTest implements Serializable{   
    
 
    private String parameterOficioId;
   
    private final String tituloPagina = "prueba";
    
    @PostConstruct
    protected void postConstruct(){
        
        System.out.println("en postconstructo TEST de OficioDetalleBeanTest");
        
        // valor inicial para la vista
        
        // obtener registro de oficio
        
        String oficioIdStr = FacesUtils.getRequestParameter("oficioId");
        
        this.parameterOficioId = oficioIdStr;
        
        System.out.println("///////////////oficioIdRecibido str"+oficioIdStr);
        
        // se debe recibir un ID
        //Integer oficioId = Integer.parseInt(oficioIdStr);             
                
    }

    public String getParameterOficioId() {
        return parameterOficioId;
    }

    public void setParameterOficioId(String parameterOficioId) {
        this.parameterOficioId = parameterOficioId;
    }
    
    public String getTituloPagina(){
                return this.tituloPagina;
    }

    
}
