
package sia.controloficios.sistema.soporte;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author 
 */
public final class PrimeUtils {


    public static void executeScript(String script){
        
        PrimeFaces.current().executeScript(String.format(";%s;",script));
        
    }   
    
  
}
