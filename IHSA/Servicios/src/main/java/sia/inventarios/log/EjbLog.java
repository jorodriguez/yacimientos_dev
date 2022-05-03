/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.log;

import sia.constantes.Constantes;


import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author AdminSia
 */
public class EjbLog {
    
    private static Logger LOGGER = LoggerFactory.getLogger(EjbLog.class);
    
    static {
        System.setProperty("current.date", Constantes.FMT_ddMMyyyyHHmmss.format(new Date()));
    }
    
    private EjbLog(){}
    
    public static void info(String mensaje){
        LOGGER.info(mensaje);
    }
    
    public static void warn(String mensaje){
        LOGGER.warn(mensaje);
    }
    
    public static void error(String mensaje){
        LOGGER.error(mensaje);
    }
    
    
}
