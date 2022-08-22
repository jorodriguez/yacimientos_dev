
package sia.modelo.oficio.vo;

import java.util.Calendar;
import java.util.Date;

/**
 * Contenedor de información de un oficio para las operaciones de consulta. 
 * 
 * Se utiliza para enviar parámetros de consulta a la capa de negocio / backend.
 * 
 * @author esapien
 */
public class OficioConsultaVo extends OficioVo {
    
    
    
    /**
     * Genera una instancia de OficioConsultaVo preparado
     * para hacer consulta de oficios con fecha de oficio del 
     * mes actual.
     * 
     * @return 
     */
    public static OficioConsultaVo instanciaMesActual() {
        
        // bean inicial de consulta
        // Se inicializa para los oficios del mes en curso
        OficioConsultaVo vo = new OficioConsultaVo();
        // mostrar inicialmente solo los oficios del presente mes

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date fechaDesde = cal.getTime();

        vo.setOficioFechaDesde(fechaDesde);
        
        return vo;
        
    }
    
}
