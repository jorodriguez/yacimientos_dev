

package sia.servicios.oficio.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import sia.excepciones.MissingRequiredValuesException;
import sia.util.UtilSia;

/**
 * Realiza las validaciones necesarias en las operaciones de gestión de 
 * seguimiento de oficios.
 *
 * @author esapien
 */
public class ValidacionSeguimientoInterceptor {

    /**
     * Valida los parametros requeridos de un usuario para activar o desactivar 
     * el seguimiento de un oficio.
     * 
     * @param context
     * @return
     * @throws Exception 
     */
    @AroundInvoke
    public Object validarRequerido(InvocationContext context) throws
            Exception {
     
        Object parameters[] = context.getParameters();
        
        String motivo = (String)parameters[1];
        
        if (UtilSia.isNullOrBlank(motivo)) {
            throw new MissingRequiredValuesException("Favor de proporcionar el motivo de (des)activación de seguimiento.");
        }
        
        return context.proceed();
    }
    
}
