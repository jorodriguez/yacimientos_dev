package lector.constantes;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class Constantes {
    
    public static final String PERSISTENCE_UNIT = "Lector-ServiciosPU";
    
    public static final Integer USUARIO_DEFAULT =1;

    public static final boolean BOOLEAN_TRUE = true;
    public static final boolean BOOLEAN_FALSE = false;
    public static final String NULL = "NULL";
    public static final String VACIO = "";
    public static final String BLANCO = " ";
    public static final String GUION = "-";
    public static final String GUION_BAJO = "_";
    public static final String PUNTO = ".";
    public static final String COMA = ",";
    public static final String COMILLA_SIMPLE = "'";
    public static final String COMILLA_DOBLE = "\"";
    public static final String COMA_COMILLAS_SIMPLES = "','";
    public static final int CERO = 0;
    public static final String NEGRO = "Negro";
    // caracteres especiales para depurar cadenas antes de su guardardo en base de datos
    public static final String CARACTER_WORD_COMILLA_DOBLE_ABRE = "“";
    public static final String CARACTER_WORD_COMILLA_DOBLE_CIERRA = "”";
    public static final String CARACTER_WORD_GUION = "–";
    // File sizes used to generate formatted label
    public static final long MEGABYTE_LENGTH_BYTES = 1048000l;
    public static final long KILOBYTE_LENGTH_BYTES = 1024l;
    public static final boolean NO_ELIMINADO = BOOLEAN_FALSE;
    public static final boolean ELIMINADO = BOOLEAN_TRUE;
    public static final String CORREO_EJEMPLO = "ejemplo@ihsa.mx";
    public static final int PLANTILLA_HTML_NOTIFICACION_LOGO_IZQUIERDA_ID = 1;
    public static final int PLANTILLA_HTML_FORMATO_REQUISICIONES_ID = 2;
    public static final int PLANTILLA_HTML_FORMATO_ORDENES_COMPRA_SERVICIO_ID = 3;
    public static final int PLANTILLA_HTML_FORMATO_CONTROL_DE_OFICIOS = 4;
       
    /**
     * Icono de vídeo wmv
     */
    public static final String ICON_VIDEO_WMV = "./resources/imagenes/icon_video_wmv.png";
    /**
     * Icono de vídeo wmv para 
     */
    public static final String ICON_VIDEO_WMV_WEB = "./resources/SiaWeb/Imagenes/icon_video_wmv.png";
    /**
     * Icono de archivo pdf
     */
    public static final String ICON_PDF = "./resources/imagenes/icon_pdf.png";
    /**
     * Icono de archivo pdf para 
     */
    public static final String ICON_PDF_WEB = "./resources/Imagenes/icon_pdf.png";
    /**
     * Extensión de archivo .pdf
     */
    public static final String ARCHIVO_EXTENSION_PDF = ".pdf";
    /**
     * Extensión de archivo .odt
     */
    public static final String ARCHIVO_EXTENSION_ODT = ".odt";
    /**
     * Extensión de archivo .odt
     */
    public static final String ARCHIVO_EXTENSION_TMP = ".tmp";
    /**
     * Extensión de archivo .wmv
     */
    public static final String ARCHIVO_EXTENSION_VIDEO_WINDOWS_MEDIA_PLAYER = ".wmv";
    /**
     * Content Type para PDF
     */
    public static final String CONTENT_TYPE_PDF = "application/pdf";
    /**
     * Content Type para ODT
     */
    public static final String CONTENT_TYPE_ODT = "application/vnd.oasis.opendocument.text";
    /**
     * Content Type para WMV
     */
    public static final String CONTENT_TYPE_WMV = "video/x-ms-wmv";

    /**
     * Path para docroot para deployments con ruta raíz en domain1/
     */
    public static final String PATH_SERVIDOR_DOCROOT = "docroot/";
    /**
     * Path para docroot para deployments con ruta raíz en domain1/config/
     */
    public static final String PATH_SERVIDOR_DOCROOT_SUBDIR = "../docroot/";
    /**
     * Path para validar deployments con ruta raíz en domain1/config/
     */
    public static final String PATH_END_SUBDIR_CONFIG = "config";
    /**
     * Identificadores de la tabla SI_PARAMETRO
     */
    public static final int PARAMETRO_LOCAL_FILES_ID = 1;

    /**
     * Se usa para ordenar de forma ASCENDENTE los querys
     */
    public static final String ORDER_BY_ASC = "ASC";
    /**
     * Se usa para ordenar de forma DESCENDENTE los querys
     */
    public static final String ORDER_BY_DESC = "DESC";
    /**
     * Formato para fecha - dd/MM/yyyy Ejemplo: 19/08/2012
     */
    public static final SimpleDateFormat FMT_ddMMyyy = new SimpleDateFormat("dd/MM/yyyy");

    public static final SimpleDateFormat FMT_ddMMyyyh_mm_a = new SimpleDateFormat("dd/MM/yyyy h:mm a");
    /**
     * Formato para fecha - yyyy/MM/dd Ejemplo: 2012/08/19
     */
    public static final SimpleDateFormat FMT_yyyyMMdd = new SimpleDateFormat("yyyy/MM/dd");
    /**
     * Formato para fecha - yyyy-MM-dd Ejemplo: 2012-08-19
     */
    public static final SimpleDateFormat FMT_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * Formato para hora - HH:mm:ss Ejemplo: 12:05:23
     */
    public static final SimpleDateFormat FMT_HHmmss = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat FMT_yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss ");
    /**
     * *
     * Formato que se usa para los archivos de log
     */
    public static final SimpleDateFormat FMT_ddMMyyyyHHmmss = new SimpleDateFormat("dd.MM.yyyy hh-mm-ss");
    /**
     * Formato para hora - h:mm Ejemplo: 12:05 (Sin Segundos)
     */
    public static final SimpleDateFormat FMT_hmm_a = new SimpleDateFormat("HH:mm ");
    /**
     * Formato texto para hora - dd 'de' MMMMM 'de' yyyy Ejemplo:
     */
    public static final SimpleDateFormat FMT_TextDate = new SimpleDateFormat("dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
    /**
     * Formato texto para hora - EEEE 'de' MMMMM 'de' yyyy
     */
    public static final SimpleDateFormat FMT_TextDateLarge = new SimpleDateFormat("EEEE dd 'de' MMMMM 'de' yyyy", new Locale("es", "ES"));
    /**
     * Formato general para Moneda ó Montos Ejemplo: 5968.90 <--Solo 2 decimales
     */
    public static final DecimalFormat formatoMoneda = new DecimalFormat("###,###,###.##");


    public static final String[] MESES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    
    public static final String RUTA_LOCAL_FILES = "/local/files/";

    public static final SimpleDateFormat FMT_dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");    
    
    public enum Etiquetas {                
                NOMBRE("NOMBRE"),                 
                DOMICILIO("DOMICILIO"),
                CLAVE_DE_ELECTOR("CLAVE DE ELECTOR"),
                CURP("CURP"),
                ANIO_DE_REGISTRO("AÑO DE REGISTRO"),
                FECHA_DE_NACIMIENTO("FECHA DE NACIMIENTO"),
                SECCION("SECCIÓN"),
                VIGENCIA("VIGENCIA"),
                SEXO("SEXO"),
                ESTADO("ESTADO"),
                MUNICIPIO("MUNICIPIO"),
                LOCALIDAD("LOCALIDAD"),
                EMISION("EMISÓN");
                
                private final String value;
                
                private Etiquetas(String value){  
                    this.value=value;  
                }  
                
    };  
    
    public static final List<String> ETIQUETAS_INE =  Arrays.asList(
                                    Etiquetas.NOMBRE.value,
                                    Etiquetas.DOMICILIO.value,
                                    Etiquetas.CLAVE_DE_ELECTOR.value,
                                    Etiquetas.CURP.value,
                                    Etiquetas.ANIO_DE_REGISTRO.value,
                                    Etiquetas.FECHA_DE_NACIMIENTO.value,
                                    Etiquetas.SECCION.value,
                                    Etiquetas.VIGENCIA.value,
                                    Etiquetas.SEXO.value,
                                    Etiquetas.ESTADO.value,
                                    Etiquetas.MUNICIPIO.value,
                                    Etiquetas.LOCALIDAD.value,
                                    Etiquetas.EMISION.value                                   
            );
    
}
