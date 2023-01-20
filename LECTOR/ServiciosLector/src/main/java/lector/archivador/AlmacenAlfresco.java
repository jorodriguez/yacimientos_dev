package lector.archivador;

import com.newrelic.api.agent.Trace;
import java.io.File;
import java.util.Properties;
import lector.alfresco.api.AlfrescoClient;
import lector.excepciones.SIAException;

/**
 * Implementación de almacén de documentos con Alfresco. En este caso se utiliza
 * el API de Alfresco para las operaciones con los archivos anexos (ETS).
 * @author mrojas
 */
public class AlmacenAlfresco extends AlmacenDocumentos {

    private final AlfrescoClient alfrescoClient = new AlfrescoClient();
    
    
    public AlmacenAlfresco(Properties config) {
        alfrescoClient.setConfig(config);
    }
    
    @Override
    public void guardarDocumento(DocumentoAnexo documento) throws SIAException {
        alfrescoClient.uploadFile(documento);
    }

    @Override
    public void borrarDocumento(DocumentoAnexo documento) throws SIAException {
        borrarDocumento(documento.getRuta() + File.separator + documento.getNombreBase());
    }

    @Override
    public void borrarDocumento(String rutaCompleta) throws SIAException {
        if(!alfrescoClient.deleteObjectFromPath(rutaCompleta)) {
            throw new SIAException("No fue posible eliminar el archivo.");
        }
    }

    
    @Trace
    @Override
    public DocumentoAnexo cargarDocumento(String rutaCompleta) throws SIAException {
        DocumentoAnexo retVal = new DocumentoAnexo(alfrescoClient.getObjectFromPath(rutaCompleta));
        return retVal;
    }

    @Override
    public void moverDocumento(DocumentoAnexo documento, String nuevaRuta) throws SIAException {
        //TODO : implementar funcionalidad
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
