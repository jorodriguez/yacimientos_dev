

package sia.controloficios.backing.bean;

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
    @Override
    protected void postConstruct() throws SIAException {
        
        getLogger().info(this, "OficioInicioBean@postConstruct");
        boolean isEditor = false;
        boolean acceso = true;
        if(getPermisos() != null){
             isEditor = getPermisos().isRolEdicionOficios();
        } else {
            acceso = false;
        }
        
        
        if (isEditor && acceso) {
            paginaPrincipal = "/vistas/oficios/bandejaEntrada";
        } else if(!isEditor && acceso){
            paginaPrincipal = "/vistas/oficios/consultar";
        } else {
            paginaPrincipal =  "/vistas/oficios/noTieneAcceso";
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
        
        return paginaPrincipal;
    }
    
    
}
