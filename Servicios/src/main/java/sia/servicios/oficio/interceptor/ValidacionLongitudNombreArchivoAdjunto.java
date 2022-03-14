
package sia.servicios.oficio.interceptor;

import com.google.common.base.Strings;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import sia.constantes.Constantes;
import sia.excepciones.InvalidValuesException;
import sia.modelo.oficio.vo.AdjuntoOficioVo;

/**
 * 
 * Valida la longitud máxima permitida del nombre de un archivo adjunto
 * para su guardado exitoso en la base de datos.
 *
 * @author esapien
 */
public class ValidacionLongitudNombreArchivoAdjunto {
    
    /**
     * 
     * @param context
     * @return
     * @throws Exception 
     */
    @AroundInvoke
    public Object validar(InvocationContext context) throws
            Exception {
        
        Object parameters[] = context.getParameters();
        
        AdjuntoOficioVo adjuntoVo = (AdjuntoOficioVo)parameters[0];
        
        String nombreArchivo = Strings.nullToEmpty(adjuntoVo.getNombre());
        
        if (nombreArchivo.length() > Constantes.OFICIOS_ARCHIVO_ADJUNTO_NOMBRE_LONGITUD_MAXIMA) {
            
                throw new InvalidValuesException(
                        "El nombre del archivo excede la longitud máxima permitida (" 
                        + Constantes.OFICIOS_ARCHIVO_ADJUNTO_NOMBRE_LONGITUD_MAXIMA + ").");
        }
        
        return context.proceed();
        
    }
    
}
