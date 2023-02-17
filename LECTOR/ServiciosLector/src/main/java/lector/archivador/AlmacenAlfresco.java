package lector.archivador;

import com.newrelic.api.agent.Trace;
import java.io.File;
import java.util.Properties;
import lector.alfresco.api.AlfrescoClient;
import lector.excepciones.LectorException;

/**
 * Implementación de almacén de documentos con Alfresco. En este caso se utiliza
 * el API de Alfresco para las operaciones con los archivos anexos (ETS).
 */
public class AlmacenAlfresco extends AlmacenDocumentos {

    private final AlfrescoClient alfrescoClient = new AlfrescoClient();
    
    
    public AlmacenAlfresco(Properties config) {
        alfrescoClient.setConfig(config);
    }
    
    @Override
    public void guardarDocumento(DocumentoAnexo documento) throws LectorException {
        alfrescoClient.uploadFile(documento);
    }

    @Override
    public void borrarDocumento(DocumentoAnexo documento) throws LectorException {
        borrarDocumento(documento.getRuta() + File.separator + documento.getNombreBase());
    }

    @Override
    public void borrarDocumento(String rutaCompleta) throws LectorException {
        if(!alfrescoClient.deleteObjectFromPath(rutaCompleta)) {
            throw new LectorException("No fue posible eliminar el archivo.");
        }
    }

    
    @Trace
    @Override
    public DocumentoAnexo cargarDocumento(String rutaCompleta) throws LectorException {
        DocumentoAnexo retVal = new DocumentoAnexo(alfrescoClient.getObjectFromPath(rutaCompleta));
        return retVal;
    }

    @Override
    public void moverDocumento(DocumentoAnexo documento, String nuevaRuta) throws LectorException {
        //TODO : implementar funcionalidad
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
