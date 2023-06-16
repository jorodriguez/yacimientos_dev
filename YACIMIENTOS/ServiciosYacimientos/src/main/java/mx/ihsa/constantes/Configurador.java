/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.constantes;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import mx.ihsa.util.UtilLog4j;

/**
 *
 */
public final class Configurador {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    private final static String CONF_FILE = "/etc/yacimientos.properties";
    private final static String DEFAULT_URL = "http://domain.mx";
    
    
    private Configurador() {
    }
    
    public static String urlSistema() {
        final String retVal = getProperty("sistema.url");
        return (retVal == null ? DEFAULT_URL : retVal);
    }
    
    
    private static String getProperty (final String property) {
        final Properties properties = new Properties();
        String retVal = null;
	FileInputStream fis = null;
        
        try {
            if (Files.exists(Paths.get(CONF_FILE))) {
                fis = new FileInputStream(CONF_FILE);
                properties.load(fis);

                retVal = properties.getProperty(property);
            } else {
               LOGGER.warn("No existe el archivo {}", CONF_FILE);
            }
            
	} catch (IOException ex) {
	    LOGGER.warn("Error al acceder a {}", CONF_FILE, ex);
	} finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.warn("No fue posible cerrar el archivo properties", ex);
                }
            }
        }
        
        return retVal;
    }
    
}
