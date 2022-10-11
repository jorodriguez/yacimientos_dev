package sia.modelo.sistema.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author mrojas
 */
@Builder
@Getter
@ToString
public class Sesion {
    
    private int id;
    private String sesionId;
    private String datosCliente;
    private String genero;
    private String modifico;
    private String puntoAcceso;
}
