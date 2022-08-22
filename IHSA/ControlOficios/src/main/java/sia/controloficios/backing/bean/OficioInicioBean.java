

package sia.controloficios.backing.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import sia.excepciones.SIAException;

/**
 *
 * @author esapien
 */
@ManagedBean
public class OficioInicioBean extends OficioBaseBean {
    
    
    private String paginaPrincipal;

    /**
     * 
     * @throws SIAException 
     */     
    //@PostConstruct
    @Override
    protected void postConstruct() throws SIAException {
       
        
        getLogger().info(this, "OficioInicioBean@postConstruct");
        
        System.out.println("||||postConstruct en oficioInicioBean");
                
        boolean isEditor = false;
        boolean acceso = true;
        if(getPermisos() != null){
             isEditor = getPermisos().isRolEdicionOficios();
        } else {
            acceso = false;
        }
        
        
        if (isEditor && acceso) {          
            paginaPrincipal = "/vistas/oficios/bandejaEntrada.xhtml?faces-redirect=true";
        } else if(!isEditor && acceso){
            paginaPrincipal = "/vistas/oficios/consultar.xhtml?faces-redirect=true";
        } else {
            paginaPrincipal =  "/vistas/oficios/noTieneAcceso.xhtml?faces-redirect=true";
        }
        System.out.println("=================Pagina principal "+paginaPrincipal);
       
    }

    /**
     * 
     * @return 
     */
    @Override
    protected boolean permisosRequeridos() {
        
        return getPermisos().isIngresarModuloOficios();
    }

    /**
     * 
     * @return 
     */
    public String paginaPrincipal() {
        
        getLogger().info(this, "Pagina Principal = " + paginaPrincipal);
        System.out.println("paginaPrincipal() = "+paginaPrincipal);
               
        return paginaPrincipal;
    }



    
}
