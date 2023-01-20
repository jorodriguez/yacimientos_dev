package lector.alfresco.api.util;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lector.modelo.SiParametro;

import lector.servicios.sistema.impl.SiParametroImpl;

/**
 * Configuration for Alfresco access. Should be loaded from database.
 * @author mrojas
 */
//Singleton
//@Startup
public class Config {

    @Inject
    private SiParametroImpl parametrosSistema;
    
    private static Properties properties;
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());
    
    @PostConstruct
    public void init() {
        loadProperties();
    }

    public Properties loadProperties() {
        if (properties == null) {
            properties = new Properties();
            
            SiParametro parametros = parametrosSistema.find(1);
            
            properties.setProperty("host", parametros.getGestDocUrlBase());
            properties.setProperty("username", parametros.getGestDocUsuario());
            properties.setProperty("password", parametros.getGestDocClave());
            
            if(!Strings.isNullOrEmpty(parametros.getGestDocPropAdic())) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> propAdic = 
                        gson.fromJson(parametros.getGestDocPropAdic(), type);
                
                for (Map.Entry<String, String> entrySet : propAdic.entrySet()) {
                    properties.setProperty(entrySet.getKey(), entrySet.getValue());
                }
            }
            
            
            LOGGER.log(Level.FINE, "Propiedades : {0}", properties.toString());
            
//            config.setProperty("site", "alfresco-api-demo");
//            config.setProperty("folder_name", "ETS");
            //config.setProperty("host", "http://localhost:8080/alfresco");
            
            
            /*InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties");
            try {
                config.load(in);
            } catch (IOException ioe) {
                LOGGER.log(Level.SEVERE, null, ioe);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }*/

        }
        return properties;
    }

    
    public String getProperty(String property) {
        return properties.getProperty(property);
    }
}
