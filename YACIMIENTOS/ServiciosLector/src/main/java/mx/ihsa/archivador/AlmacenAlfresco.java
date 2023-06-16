package mx.ihsa.archivador;

import com.newrelic.api.agent.Trace;
import java.io.File;
import java.util.Properties;
import mx.ihsa.alfresco.api.AlfrescoClient;
import mx.ihsa.excepciones.GeneralException;

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
    public void guardarDocumento(DocumentoAnexo documento) throws GeneralException {
        alfrescoClient.uploadFile(documento);
    }

    @Override
    public void borrarDocumento(DocumentoAnexo documento) throws GeneralException {
        borrarDocumento(documento.getRuta() + File.separator + documento.getNombreBase());
    }

    @Override
    public void borrarDocumento(String rutaCompleta) throws GeneralException {
        if(!alfrescoClient.deleteObjectFromPath(rutaCompleta)) {
            throw new GeneralException("No fue posible eliminar el archivo.");
        }
    }

    
    @Trace
    @Override
    public DocumentoAnexo cargarDocumento(String rutaCompleta) throws GeneralException {
        DocumentoAnexo retVal = new DocumentoAnexo(alfrescoClient.getObjectFromPath(rutaCompleta));
        return retVal;
    }

    @Override
    public void moverDocumento(DocumentoAnexo documento, String nuevaRuta) throws GeneralException {
        //TODO : implementar funcionalidad
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
