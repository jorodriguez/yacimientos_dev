package mx.ihsa.util;

import com.google.common.base.Strings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mx.ihsa.constantes.Constantes;

/**
 * Validar que el nombre del archivo no contenga caracteres prohibidos:
 * <ul>
 *   <li>Asteriscos (*)</li>
 *   <li>Diagonal (/)</li>
 *   <li>Símbolo de interrogación (?)
 * </ul>
 * 
 * Cualquier otro caracter se considera válido.
 * @author mrojas
 */
public class ValidadorNombreArchivo {
//    private final Pattern pattern = Pattern.compile("[^a-z\\d\\(\\)\\ \\.\\+\\,\\-\\_]");
    private final Pattern pattern = Pattern.compile("[\\*\\/\\?]");
    private Matcher matcher;
    private StringBuffer caracteresNoValidos;

    /**
     * Valida que el nombre se apegue a la regla de no contener caracteres inválidos.
     * @param nombreArchivo El nombre del archivo a validar.
     * @return True en caso de que sea un nombre válido, false en caso contrario.
     */
    public boolean isNombreValido(String nombreArchivo) {
        matcher = pattern.matcher(nombreArchivo.toLowerCase());
        boolean retVal = matcher.find();

        if (retVal) {
            caracteresNoValidos = new StringBuffer();
            caracteresNoValidos.append(nombreArchivo.substring(matcher.start(), matcher.end()));

            while (matcher.find()) {
                caracteresNoValidos.append(nombreArchivo.substring(matcher.start(), matcher.end()));
            }
        }

        return !retVal;
    }
    
    /**
     * Devuelve la lista de los caracteres no válidos que se encontraron en el
     * nombre del archivo.
     * @return La lista de caracteres no válidos, si es que los hubo, o una cadena
     * en blanco.
     */
    public String getCaracteresNoValidos() {
        String retVal = Constantes.VACIO;
        
        
        if(!Strings.isNullOrEmpty(caracteresNoValidos.toString())) {
            retVal = caracteresNoValidos.toString();
        }
        
        return retVal;
    }
}
