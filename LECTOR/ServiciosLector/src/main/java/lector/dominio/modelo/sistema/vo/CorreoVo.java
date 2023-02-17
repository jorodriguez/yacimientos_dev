

package lector.dominio.modelo.sistema.vo;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Contiene los elementos para el envío de un correo.
 *
 */
@Getter
@Setter
public class CorreoVo {
    
    // version cadenas
    public String para;
    public String cc;
    public String cco;
    public String asunto;
    public String mensaje;
    
    // sets
    public Set<String> setPara;
    public Set<String> setCc;
    public Set<String> setCco;
    
}
