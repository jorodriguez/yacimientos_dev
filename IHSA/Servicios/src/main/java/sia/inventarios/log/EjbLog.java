/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.log;

import sia.constantes.Constantes;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.Date;

/**
 *
 * @author AdminSia
 */
public class EjbLog {
    static {
        System.setProperty("current.date", Constantes.FMT_ddMMyyyyHHmmss.format(new Date()));
    }
    
    public static void info(String mensaje){
        final Logger logger = LogManager.getLogger(EjbLog.class.getName());
        BasicConfigurator.configure();
        logger.log(Priority.DEBUG, mensaje);
    }
    
    public static void warn(String mensaje){
        final Logger logger = LogManager.getLogger(EjbLog.class.getName());
        BasicConfigurator.configure();
        logger.log(Priority.WARN, mensaje);
    }
    
    public static void error(String mensaje){
        final Logger logger = LogManager.getLogger(EjbLog.class.getName());
        BasicConfigurator.configure();
        logger.log(Priority.ERROR, mensaje);
    }
    
    
}
