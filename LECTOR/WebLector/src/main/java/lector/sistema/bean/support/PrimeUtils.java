
package lector.sistema.bean.support;

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
