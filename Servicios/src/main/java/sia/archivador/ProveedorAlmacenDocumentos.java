/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.archivador;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import sia.modelo.SiParametro;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;

/**
 * Proveedor de almacén de documentos. Administra y devuelve el acceso adecuado
 * al tipo de almacenamiento que se esté utilizando.
 *
 * @author mrojas
 */
@LocalBean
public class ProveedorAlmacenDocumentos {

    @Inject
    private SiParametroImpl parametrosSistema;
    private SiParametro parametros;

    private final static String ALM_SIS_ARCH = "LFS";
    private final static String ALM_ALFRESCO = "ALF";
    
    private final static UtilLog4j LOGGER = UtilLog4j.log;

    
    public AlmacenDocumentos getAlmacenDocumentos() {
        AlmacenDocumentos almacen = null;

        parametros = parametrosSistema.find(1);

        if (ALM_SIS_ARCH.equals(parametros.getTipoAlmacenAdjuntos())) {
            LOGGER.info(this, "Utilizando sistema de archivos como almacén de documentos.");
            almacen = new AlmacenSistemaArchivos();
            almacen.setRaizAlmacen(parametrosSistema.find(1).getUploadDirectory());
        } else if (ALM_ALFRESCO.equals(parametros.getTipoAlmacenAdjuntos())) {
            LOGGER.info(this, "Utilizando Alfresco como almacén de documentos.");
            almacen = new AlmacenAlfresco(buildProperties());
        } else {
            throw new IllegalArgumentException("Tipo de almacén de documentos desconocido.");
        }

        return almacen;
    }

    private Properties buildProperties() {
        Properties config = new Properties();

        parametros = parametrosSistema.find(1);

        config.setProperty("host", parametros.getGestDocUrlBase());
        config.setProperty("username", parametros.getGestDocUsuario());
        config.setProperty("password", parametros.getGestDocClave());

        if (!Strings.isNullOrEmpty(parametros.getGestDocPropAdic())) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> propAdic
                    = gson.fromJson(parametros.getGestDocPropAdic(), type);

            for (Map.Entry<String, String> entrySet : propAdic.entrySet()) {
                config.setProperty(entrySet.getKey(), entrySet.getValue());
            }
        }
        
        return config;
    }
}
