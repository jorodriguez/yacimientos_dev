
package sia.modelo.oficio.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Contiene la información acerca de los resultados de una consulta de oficios.
 * 
 *
 * @author esapien
 */
@Getter
@Setter
public class ResultadosConsultaVo {
    
    private List<OficioPromovibleVo> resultados;
    
    private boolean cantidadMaximaExcedida;
    
    private int cantidadOriginal;
    
}
