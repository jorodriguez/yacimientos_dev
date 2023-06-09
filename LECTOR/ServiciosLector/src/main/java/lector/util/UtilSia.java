
package lector.util;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lector.constantes.Constantes;

/**
 * Utilerías comunes de uso general.
 *
 * @author nlopez
 */
public class UtilSia {
    
    private static Gson gson= new Gson();

    /**
     * @return the gson
     */
    public static Gson getGson() {
        return gson;
    }

    public static String getUrl(HttpServletRequest request){
        String url = request.getScheme()+"://" + request.getServerName();
        if(request.getServerPort()!=80){
           url += ":" +request.getServerPort();
        }
        return url;
    }
    
    
    /**
     * Indica si la cadena proporcionada es nula o vacía (cadena vacía o espacios).
     * 
     * @param str
     * @return 
     */
    public static boolean isNullOrBlank(String str) {
        
        return Strings.nullToEmpty(str).trim().isEmpty();
        
    }
    
    
    /**
     * 
     * @param list
     * @return 
     */
    public static boolean isNullOrEmpty(List<?> list) {
        
        return list == null || list.isEmpty();
        
    }
    
    
    /**
     * Indica si la referencia tiene un valor mayor que cero. Valida por nulos.
     * 
     * @param value
     * @return 
     */
    public static boolean greaterThanZero(Integer value) {
        
        return value != null && value > 0;
        
    }
    
    /**
     * 
     * @return 
     */
    public static String getFechaActual_ddMMyyy() {
        
        String fechaActual = Constantes.FMT_ddMMyyy.format(new Date());
        
        return fechaActual;
        
    }
    
    
    /**
     * Obtiene el valor numérico de tipo entero para casos válidos y no nulos.
     * 
     * @param value
     * @return 
     */
    public static Integer stringToIntegerWithNull(String value) {
        
        Integer result = null;
        
        if (value != null) {
            
            try {
                result = Integer.parseInt(value);
            } catch(Exception e) {
                // para validar vs no números
            }
        }
        
        return result;
    }

    /**
     * Genera una lista de valores separados por comas, opcionalmente con comillas simples.
     * 
     * Ejemplos: 
     * 
     * 1,4,2,50,6,70,100
     * 'UNO','DOS','CUATRO'
     * 
     * @param values Lista con los valores a convertir a una cadena separada por comas.
     * @param includeSingleQuotes Bandera para indicar si se deberán utilizar prefijos y sufijos
     * de comillas simples para los valores
     * @return 
     */
    public static String toCommaSeparatedString(List<?> values, boolean includeSingleQuotes) {
        
        String separador;
        String prefixSuffix;
        
        StringBuilder sb = new StringBuilder();
        
        if (!isNullOrEmpty(values)) {
            if (includeSingleQuotes) {
                separador = Constantes.COMA_COMILLAS_SIMPLES;
                prefixSuffix = Constantes.COMILLA_SIMPLE;
            } else {
                separador = Constantes.COMA;
                prefixSuffix = Constantes.VACIO;
            }

            Joiner joiner = Joiner.on(separador).skipNulls();

            sb.append(prefixSuffix);
            sb.append(joiner.join(values));
            sb.append(prefixSuffix);
        }
        
        return sb.toString();
    }
    
    /**
     * 
     * @param str Cadena con valores separados por comas
     * @return 
     */
    public static List<String> toList(String str) {
        
        List<String> items;
        
        if (!UtilSia.isNullOrBlank(str)) {
            items = Arrays.asList(str.split("\\s*,\\s*"));
        } else {
            items = new ArrayList<String>();
        }
        
        return items;
    }
    
    
    /**
     * 
     * @param str
     * @return 
     */
    public static int stringLength(String str) {
        
        int resultado;
        
        if (str == null) {
            resultado = -1;
        } else {
            resultado = str.length();
        }
        
        return resultado;
        
    }
    
    
    /**
     * 
     * Depura la cadena para remplazar y/o remover los caracteres que no se guardan
     * correctamente en la base de datos.
     * 
     * @param cadena
     * @return 
     */
    public static String depurarCaracteresBaseDatos(String cadena) {
        
        if (!isNullOrBlank(cadena)) {
            cadena = cadena.replace(Constantes.CARACTER_WORD_COMILLA_DOBLE_ABRE, Constantes.COMILLA_DOBLE);
            cadena = cadena.replace(Constantes.CARACTER_WORD_COMILLA_DOBLE_CIERRA, Constantes.COMILLA_DOBLE);
            cadena = cadena.replace(Constantes.CARACTER_WORD_GUION, Constantes.GUION);
        }
        
        return cadena;
        
    }
    
    public static String camelToSnake(String str)
    {
 
        String result = "";
 
        char c = str.charAt(0);
        result = result + Character.toLowerCase(c);
 
        for (int i = 1; i < str.length(); i++) {
 
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result
                    = result
                      + Character.toLowerCase(ch);
            }
 
            else {
                result = result + ch;
            }
        }
 
        return result;
    }
    
    
    
}
