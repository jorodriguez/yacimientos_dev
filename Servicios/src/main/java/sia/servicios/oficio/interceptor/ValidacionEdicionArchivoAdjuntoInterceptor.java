
package sia.servicios.oficio.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import sia.excepciones.MissingRequiredValuesException;
import sia.modelo.oficio.vo.AdjuntoOficioVo;
import sia.modelo.oficio.vo.MovimientoVo;
import sia.util.UtilSia;

/**
 * 
 * Interceptor para las operaciones de sustitución de archivo adjunto en el
 * módulo de Oficios
 *
 * @author esapien
 */
public class ValidacionEdicionArchivoAdjuntoInterceptor {
    
    
    
    /**
     * 
     * @param context
     * @return
     * @throws Exception 
     */
    @AroundInvoke
    public Object validarRequerido(InvocationContext context) throws
            Exception {
        
        Object parameters[] = context.getParameters();
        
        MovimientoVo movimientoVo = (MovimientoVo)parameters[1];
        
        AdjuntoOficioVo adjuntoVo = movimientoVo.getAdjunto();
        
        String motivo = (String)parameters[2];
        
        // validar archivo
        if (adjuntoVo == null || adjuntoVo.getId() == null || adjuntoVo.getId() <= 0) {
            
            throw new MissingRequiredValuesException("Proporcione un nuevo archivo adjunto.");
            
        }
        
        // validar motivo
        if (UtilSia.isNullOrBlank(motivo)) {
            throw new MissingRequiredValuesException("Proporcione un motivo de sustitución de archivo adjunto.");
        }
        
        return context.proceed();
        
    }
    
}
