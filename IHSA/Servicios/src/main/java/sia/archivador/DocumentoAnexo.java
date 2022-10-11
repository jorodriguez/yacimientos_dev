package sia.archivador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;

/**
 * Documento para anexar a un repositorio de documentos, ya sea en un sistema de
 * archivos físico o en un sistema de administración de contenidos.
 * @author mrojas
 */
@Getter
@Setter
public class DocumentoAnexo {
    
    private String nombreBase;
    private String ruta;
    private String tipoMime;
    private byte[] contenido;
    private String uuid;
    
    public DocumentoAnexo(byte[] contenido) throws SIAException {
        if(contenido == null) {
            throw new SIAException("El contenido para el documento anexo está vacío.");
        } else {
            this.contenido = contenido;
//            setTipoMime(Utilerias.obtenerTipoMIME(contenido));
        }
    }
    
    public DocumentoAnexo(File file) throws IOException, SIAException {
        if(file == null) {
            throw new SIAException("El contenido para el documento anexo es nulo.");
        } else {
            extraerInfo(file);
        }
    }
    
    /**
     * Devuelve el tamaño del archivo en bytes.
     * @return El tamaño en bytes.
     */
    public int getTamanio() {
        int tamanio = 0;
        
        if(contenido != null) {
            tamanio = contenido.length;
        }
        
        return tamanio;
    }

    /**
     * Extrae la información relevante del archivo, como el nombre base, la 
     * extensión y el tipo MIME.
     * @param file El archivo que se desea almacenar
     * @throws IOException En caso de ocurrir algún problema al recuperar el 
     * arreglo de bytes que conforma el archivo 
     */
    private void extraerInfo(File file) throws IOException {
        String nombre = FilenameUtils.getBaseName(file.getName());
        String extension = FilenameUtils.getExtension(file.getName());
        uuid = UUID.randomUUID().toString();
        
        setContenido(FileUtils.readFileToByteArray(file));
        setNombreBase(nombre + "UUID" + uuid + Constantes.PUNTO + extension);
        setTipoMime(Files.probeContentType(file.toPath()));
//        setTipoMime(Utilerias.obtenerTipoMIME(getContenido()));
    }
}
