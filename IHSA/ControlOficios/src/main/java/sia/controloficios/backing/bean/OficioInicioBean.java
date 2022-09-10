

package sia.controloficios.backing.bean;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import jdk.jfr.Name;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.excepciones.SIAException;

/**
 *
 * @author esapien
 */
//@ManagedBean
@Named(value = "oficioInicioBean")
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
      
              
    }
    
    public String iniciarPermisos(){
        
        System.out.println("Iniando permisos de usuario");               
        
        boolean isEditor = false;
        boolean acceso = true;
        if(getPermisos() != null){
             isEditor = getPermisos().isRolEdicionOficios();
        } else {
            acceso = false;
        }        
        
        if (isEditor && acceso) {          
            paginaPrincipal = "/vistas/oficios/bandejaEntrada.xhtml?faces-redirect=true";
            //paginaPrincipal = "/vistas/oficios/bandejaEntrada";
        } else if(!isEditor && acceso){
            paginaPrincipal = "/vistas/oficios/consultar.xhtml?faces-redirect=true";
            //paginaPrincipal = "/vistas/oficios/consultar";
        } else {
            paginaPrincipal =  "/vistas/oficios/noTieneAcceso.xhtml?faces-redirect=true";
            //paginaPrincipal =  "/vistas/oficios/noTieneAcceso";
        }
        System.out.println("=================Pagina principal "+paginaPrincipal);
        
        return paginaPrincipal;
    }
    
    public void redireccionarPaginaInicial(){
        try{
            
            final String pagina = paginaPrincipal != null ? paginaPrincipal : "/vistas/oficios/noTieneAcceso";
            
            System.out.println("Redireccion de la pagina principal "+pagina);
            
            FacesUtils.redireccionar(pagina);
            
        }catch(IOException ex){
            FacesUtils.addErrorMessage("Ups, al parecer hay conflictos con los permisos de tu usuario, por favor contacta al equipo de soporte.");
        }
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
