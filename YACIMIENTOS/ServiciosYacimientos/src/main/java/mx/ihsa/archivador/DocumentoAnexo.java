package mx.ihsa.archivador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import mx.ihsa.constantes.Constantes;
import mx.ihsa.excepciones.GeneralException;

/**
 * Documento para anexar a un repositorio de documentos, ya sea en un sistema de archivos físico o
 * en un sistema de administración de contenidos.
 *
 */
@Getter
@Setter
@Slf4j
public class DocumentoAnexo {

    private String nombreBase;
    private String ruta;
    private String tipoMime;
    private byte[] contenido;
    private String uuid;
    private String prettySize;

    public DocumentoAnexo(byte[] contenido) throws GeneralException {
        if (contenido == null) {
            throw new GeneralException("El contenido para el documento anexo está vacío.");
        } else {
            this.contenido = contenido;
            extraerInfo();
        }
    }

    public DocumentoAnexo(File file) throws IOException, GeneralException {
        if (file == null) {
            throw new GeneralException("El contenido para el documento anexo es nulo.");
        } else {
            extraerInfo(file);
        }
    }

    /**
     * Devuelve el tamaño del archivo en bytes.
     *
     * @return El tamaño en bytes.
     */
    public int getTamanio() {
        int tamanio = 0;

        if (contenido != null) {
            tamanio = contenido.length;
        }

        return tamanio;
    }

    /**
     * Extrae la información relevante del archivo, como el nombre base, la extensión y el tipo
     * MIME.
     *
     * @param file El archivo que se desea almacenar
     * @throws IOException En caso de ocurrir algún problema al recuperar el arreglo de bytes que
     * conforma el archivo
     */
    private void extraerInfo(File file) throws IOException {
        String nombre = FilenameUtils.getBaseName(file.getName());
        String extension = FilenameUtils.getExtension(file.getName());
        uuid = UUID.randomUUID().toString();

        setContenido(FileUtils.readFileToByteArray(file));
        setNombreBase(nombre + "UUID" + uuid + Constantes.PUNTO + extension);
        setTipoMime(Files.probeContentType(file.toPath()));
        setPrettySize(calculatePrettySize());
    }

    private void extraerInfo() {
        setPrettySize(calculatePrettySize());

        try {
            //setTipoMime(URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(getContenido())));
            setTipoMime(new Tika().detect(getContenido()));
        } catch (Exception e) {
            log.warn("*** While getting MIME type.", e);
        }
    }

    private String calculatePrettySize() {
        String retVal;
        String suffix;

        final var format = new DecimalFormat();
        format.setParseIntegerOnly(true);
        format.setMaximumIntegerDigits(10);
        format.setMaximumFractionDigits(0);

        int fileSize = getTamanio();

        if (fileSize >= 1_073_741_824) {
            fileSize = fileSize / 1024 / 1024 / 1024;
            suffix = " GB";
        } else if (fileSize >= 1_048_576) {
            fileSize = fileSize / 1024 / 1024;
            suffix = " MB";
        } else if (fileSize >= 1024) {
            fileSize = fileSize / 1024;
            suffix = " KB";
        } else {
            suffix = " B";
        }

        try {
            retVal = format.format(fileSize);
        } catch (Exception e) {
            log.warn("*** Al formatear el tamaño del archivo {}", getNombreBase(), e);
            retVal = fileSize + "";
        }

        return retVal + suffix;
    }
}
