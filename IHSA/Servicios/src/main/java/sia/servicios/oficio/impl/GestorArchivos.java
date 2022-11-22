

package sia.servicios.oficio.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Calendar;
import sia.constantes.Constantes;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.util.UtilLog4j;


/**
 * Contiene todas las operaciones relacionadas con la creación y manejo de 
 * archivos y operaciones de entrada/salida en disco del módulo de Control 
 * de Oficios.
 * 
 *
 * @author esapien
 */
public class GestorArchivos {
    
    
    //private final static Logger logger = Logger.getLogger(GestorArchivos.class.getName());
    
    //private String usuarioId;
    
    /**
     * 
     * Prefijo para los archivos temporales que se generen durante el uso
     * del sistema. Los archivos deberán tener este prefijo con el fin de 
     * que el mecanismo de limpieza los identifique y los borre.
     * 
     * <p/>
     * El mecanismo de limpieza está ligado al ciclo de vida del managed bean 
     * en el método @PreDestroy.
     * 
     */
    private String prefijoArchivoTemporal;
    
    /**
     * Contiene la ruta base de los archivos adjuntos para la plataforma SIA.
     */
    //private String rutaRaizAdjuntos;
    
    private String rutaDirectorioOficios;
    
    
    /**
     * Contiene la ruta de los archivos temporales generados para el visor
     * de archivos adjuntos.
     * 
     */
    private String rutaVisorTemporales;
    
    
    /**
     * Constructor
     * 
     * @param usuarioId 
     */
    public GestorArchivos(String rutaRaizAdjuntos, String usuarioId) {
        
        //this.rutaRaizAdjuntos = rutaRaizAdjuntos;
        
        this.rutaDirectorioOficios 
                = rutaRaizAdjuntos + Constantes.OFICIOS_PATH_RELATIVO_OFICIOS;
        
        this.rutaVisorTemporales 
                = Constantes.PATH_SERVIDOR_DOCROOT + Constantes.OFICIOS_PATH_ARCHIVOS_TEMP;
        
        //this.usuarioId = usuarioId;
        
        this.prefijoArchivoTemporal 
                = Constantes.OFICIOS_PREFIJO_ARCHIVOS_TEMPORALES 
                + usuarioId
                + Constantes.GUION_BAJO;
    }
    
    
    /**
     * Genera un archivo temporal a partir de un archivo guardado por el componente 
     * FileEntry de IceFaces como resultado de una carga (upload) de un archivo.
     * <p/>
     * El archivo temporal generado es nombrado con un prefijo para identificarlo
     * como un archivo temporal para ser borrado durante el proceso de limpieza
     * del managed bean.
     * <p/>
     * El archivo guardado inicialmente tiene el siguiente formato:
     * "ice_file_[consecutivo].tmp", ejemplo: ice_file_3334633903818323012.tmp.
     * <p/>
     * Es nombrado a este formato: 
     * "[temp]_[usuarioId]_[nombre_inicial]".
     * Ejemplo: temp_PRUEBA_ice_file_3334633903818323012.tmp.
     * 
     * 
     * @param archivo 
     */
    public File generarArchivoTemporal(File archivo) {
        
        // renombrar para marcar archivo como temporal hasta que se guarde el registro

        String nombreInicial = archivo.getName();

        String nombreTemporal = this.prefijoArchivoTemporal + nombreInicial;

        File archivoTemporal = new File(this.rutaDirectorioOficios + nombreTemporal);

        archivo.renameTo(archivoTemporal);
        
        return archivoTemporal;

    }
    
    
    /**
     * 
     * Renombra un archivo temporal para ser registrado como archivo adjunto 
     * de un oficio. El nombre de archivo tiene el siguiente formato:
     * 
     * [prefijo_oficio]_[milisegundos].tmp
     * 
     * Ejemplo: oficio_1403898185173.tmp
     * 
     * 
     * @param archivoTemporal
     * @param tipoArchivo
     * @return 
     */
    public File restaurarArchivoOficio(File archivoTemporal, String tipoArchivo) {
        
        // remover prefijo temporal
        
        String milisegundos = String.valueOf(Calendar.getInstance().getTimeInMillis());
        
        /*String extension = null;
        
        if (Constantes.CONTENT_TYPE_PDF.equalsIgnoreCase(tipoArchivo)) {
            extension = Constantes.ARCHIVO_EXTENSION_PDF;
        } else if (Constantes.CONTENT_TYPE_ODT.equalsIgnoreCase(tipoArchivo)) {
            extension = Constantes.ARCHIVO_EXTENSION_ODT;
        }*/
        
        String extension = Constantes.ARCHIVO_EXTENSION_TMP;
        
        String nombreArchivoFinal = Constantes.OFICIOS_PREFIJO_ARCHIVO_OFICIO + milisegundos + extension;
        
        String rutaArchivoFinal = this.rutaDirectorioOficios + nombreArchivoFinal;

        File archivoFinal = new File(rutaArchivoFinal);

        boolean renombrado = archivoTemporal.renameTo(archivoFinal);
        
        getLogger().info(this, "renombrado = " + renombrado);
        
        return archivoFinal;
        
    }
    
    /**
     * Remueve los archivos temporales que se encuentren en la ubicación
     * de archivos de Oficios.
     * 
     */
    public void borrarArchivosOficioTemporales() {
        this.borrarArchivosTemporales(this.getRutaDirectorioOficios());
        
    }
    
    
    /**
     * 
     */
    public void borrarArchivosVisorTemporales() {
        
        this.borrarArchivosTemporales(rutaVisorTemporales);
        
    }
    
    /**
     * Borra todos los archivos temporales en el directorio especificado.
     * 
     * El archivo temporal tiene el prefijo "temp_[userId]_*".
     * 
     * @param directorio 
     */
    private void borrarArchivosTemporales(String directorio) {
        
        final File folder = new File(directorio);
        
        final File[] files = folder.listFiles(new FilenameFilter() {

            
            public boolean accept(final File dir,
                    final String name) {
                return name.startsWith(prefijoArchivoTemporal);
            }
        });
        
        if (files != null && files.length > 0) {
            for (final File file : files) {
                if (!file.delete()) {
                    getLogger().warn(this, "No se pudo borrar archivo.");
                }
            }
        }
        
    }
    
    

    /**
     * 
     * @return 
     */
    public String getRutaDirectorioOficios() {
        return rutaDirectorioOficios;
    }
    
    /**
     * Temporal
     * 
     * @throws IOException 
     */
    /*private void printDirectoryContents(File directory) throws IOException {
        
        getLogger().info(this, "@printDirectoryContents()");
        
        File[] files = directory.listFiles();
        
        for (File file : files) {
            String path = file.getCanonicalPath();
            if (file.isDirectory()) {
                getLogger().info(this, "directory: " + path);
            } else {
                getLogger().info(this, "     path: " + path);
            }
        }
    }*/

    /**
     * Genera una copia temporal de un archivo adjunto en la ruta válida
     * para poder ser accedido por el componente del visor de archivos 
     * adjuntos.
     * <p/>
     * La ruta válida es bajo el directorio 'docroot' en la ruta del 
     * deployment actual.
     * 
     * 
     * @param adjunto
     * @param usuarioId 
     */
    public void generarCopiaTemporalVisor(AdjuntoOficioVo adjunto, String usuarioId) {
        
        getLogger().info(this, "@generarCopiaTemporalVisor");

        try {
            
            // obtener archivo adjunto

            File from = new File(adjunto.getUrl());

            // Generar copia temporal del archivo bajo docroot del servidor para 
            // poder ser accesado por el componente del visor de archivos.
            // Nombrar con prefijo 'temp_[userId]_' para poder ser borrado 
            // en el proceso de limpieza del PreDestroy del bean.

            String nombreTemporal = this.prefijoArchivoTemporal + from.getName();

            // ruta para visor
            String urlVisor =
                    Constantes.OFICIOS_PATH_ARCHIVOS_TEMP
                    + nombreTemporal;
            
            // Determinar la ruta del docroot
            // En algunos deployments identificados la raíz del deployment 
            // actual se encuentra en "domain1/", mientras que en otros está 
            // en "domain1/config/", por lo que en este caso para acceder al 
            // "docroot" es necesario subir al directorio padre
            
            String pathDocroot;
            
            File directorioActual = new File(Constantes.PUNTO);
            
            if (directorioActual.getCanonicalPath().toLowerCase()
                    .endsWith(Constantes.PATH_END_SUBDIR_CONFIG)) {
                pathDocroot = Constantes.PATH_SERVIDOR_DOCROOT_SUBDIR;
            } else {
                pathDocroot = Constantes.PATH_SERVIDOR_DOCROOT;
            }
            
            getLogger().info(this, 
                    "directorio raíz actual = " + directorioActual.getCanonicalPath() + ", "
                    + "pathDocroot = " + pathDocroot);
            
            // ruta completa para guardado y borrado
            String urlTemporal = pathDocroot + urlVisor;

            File to = new File(urlTemporal);

            getLogger().info(this, "url from = " + adjunto.getUrl());
            getLogger().info(this, 
                    "url visor = " + urlVisor + ", "
                    + "url temporal = " + urlTemporal);

            Files.copy(from.toPath(), to.toPath());

            adjunto.setUrlVisorArchivoTemporal(urlVisor);
            adjunto.setUrlArchivoTemporal(urlTemporal);

        } catch (FileAlreadyExistsException ex) {
            getLogger().error(this, "Error al copiar, archivo ya existente.", ex);
            //ex.printStackTrace(System.out);
        } catch (IOException ex) {
            getLogger().error(this, "Error al generar archivo adjunto temporal.", ex);
            //ex.printStackTrace(System.out);
        }
    }
    

    /**
     * 
     * @return 
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
    }    
    
    
    
    
}
