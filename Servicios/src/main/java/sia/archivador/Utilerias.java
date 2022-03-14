package sia.archivador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import sia.constantes.Constantes;
import sia.util.UtilLog4j;

/**
 * Utilerias para archivos anexos.
 *
 * @author mrojas
 */
public class Utilerias {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    public static String obtenerNombreBase(Path rutaCompleta) {
        String nombreBase = Constantes.VACIO;

        if (rutaCompleta != null && rutaCompleta.getFileName() != null) {
            nombreBase = rutaCompleta.getFileName().toString();
        }
        return nombreBase;
    }

    public static String obtenerTipoMIME(InputStream flujo) {
        return getType(TikaInputStream.get(flujo));
    }

    public static String obtenerTipoMIME(byte[] contenidoArchivo) {
        return getType(TikaInputStream.get(contenidoArchivo));
    }

    public static String obtenerTipoMIME(File archivo) throws FileNotFoundException {
        return getType(TikaInputStream.get(archivo));
    }

    private static String getType(TikaInputStream inputStream) {
        String retVal = "application/octet-stream";
        TikaConfig tika = null;
        Metadata metadata = null;
        MediaType mimeType = null;
        
        try {
            tika = new TikaConfig();
            metadata = new Metadata();
            mimeType = tika.getDetector().detect(inputStream, metadata);

            retVal = mimeType.getBaseType().toString();
            
        } catch (IOException ex) {
            LOGGER.error(ex);
        } catch (TikaException ex) {
            LOGGER.error(ex);
        } finally {
            tika = null;
            metadata = null;
            mimeType = null;
        }

        return retVal;
    }

    private Utilerias() {
    }
}
