/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.constantes;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
public final class Configurador {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    private final static String CONF_FILE = "/etc/sia.properties";
    private final static String DEFAULT_URL = "http://sia.ihsa.mx";
    private final static String ARCHIVOS_PORTAL = "soporte@kubicsoft.com";
    private final static String DEFAULT_CORREO = "soportesia@ihsa.mx";
    private final static String DEFAULT_API_MENSAJERIA_EXTERNA = "http://localhost:8889/notificaciones/token";
    
    
    private Configurador() {
    }
    
    public static String emailArchivosPortal() {
        final String retVal = getProperty("notificacion.archivos_portal");
        return (retVal == null ? ARCHIVOS_PORTAL : retVal);
    }
    
    public static String emailFacturaAvanzia() {
        final String retVal = getProperty("sia.factura.avanzia");
        return (retVal == null ? ARCHIVOS_PORTAL : retVal);
    }
     
    public static String urlSia() {
        final String retVal = getProperty("sia.url");
        return (retVal == null ? DEFAULT_URL : retVal);
    }
    
    public static String inventarioImpresoraUrl() {
        final String retVal = getProperty("inventario.impresora.url");
        return (retVal == null ? DEFAULT_URL : retVal);
    }
    
    public static String notificacionSemaforo() {
        final String retVal = getProperty("notificacion.semaforo");
        return (retVal == null ? DEFAULT_CORREO : retVal);
    }
    
    public static String urlSiaNavision(String urlTemp, String fileName, String fileExt) {
        final String retVal = getProperty("sia.navision.excel");
        return (fileName != null && !fileName.isEmpty() && fileExt != null && !fileExt.isEmpty() && retVal == null 
                ? urlTemp+fileName+fileExt : retVal+fileName+fileExt);
    }
    
    public static boolean validarPresupuesto(){
        final String retVal = getProperty("sia.validacion.presupuesto");
        return (retVal == null ? false : "TRUE".equals(retVal.toUpperCase()));
    }
    
    public static boolean validarConvenio(){
        final String retVal = getProperty("sia.validacion.convenio");
        return (retVal == null ? false : "TRUE".equals(retVal.toUpperCase()));
    }
    
    public static boolean isValidarFacturaVsSat() {
        final String retVal = getProperty("sia.validacion.factura_sat");
        return (retVal == null ? false : "TRUE".equals(retVal.toUpperCase()));
    }
    
    public static boolean bloquearFacturas() {
        final String retVal = getProperty("sia.bloquearfacturas");
        return (retVal == null ? false : "TRUE".equals(retVal.toUpperCase()));
    }
    
    public static String bloquearFacturasMsg() {
        final String retVal = getProperty("sia.bloquearfacturas.msg");
        return (retVal == null ? "La recepción de facturas se encuentra cerrada en estos momentos." : retVal);
    }
    
    public static String getUrlApiMensajeriaExterna() {
        final String retVal = getProperty("sia.api.mensajeria.externa");
        return (retVal == null ? DEFAULT_API_MENSAJERIA_EXTERNA : retVal);
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
                    LOGGER.warn("No fue posible cerrar el archivo /etc/sia.properties", ex);
                }
            }
        }
        
        return retVal;
    }
    
    public static String notificacionRecepcionFacturas() {
        final String retVal = getProperty("sia.recepcion.factura");
        return (retVal == null ? DEFAULT_CORREO : retVal);
    }
}
