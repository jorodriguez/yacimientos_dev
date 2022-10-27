/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;




import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import sia.modelo.requisicion.vo.RequisicionView;
import sia.servicios.requisicion.impl.RequisicionImpl;
import static sia.constantes.Constantes.RUTA_COMPRAS_DESDE_REQ;
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
    
    @Getter
    private List<RequisicionView> listaRequisiciones;
    
    
            
    public PrincipalViewBean() { }

    @PostConstruct
    public void iniciar() {
        //loaders
        this.cargarListaRequisiciones();
        
    }

    private void cargarListaRequisiciones() {
        
        this.listaRequisiciones = requisicionImpl.getUltimasRequisicionesModificadas(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo());
        
    }
    
      
    public String getRutaModuloComprasDeRequ(){
        return RUTA_COMPRAS_DESDE_REQ;
    }
    
    public String getUrlSolicitarRequisicion(){
        return "vistas/SiaWeb/Requisiciones/CrearRequisicion";
    }
    
}
