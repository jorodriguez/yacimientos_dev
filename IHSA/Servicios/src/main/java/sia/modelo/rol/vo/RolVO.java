

package sia.modelo.rol.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.permiso.vo.PermisoVo;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author nlopez
 */
@Getter
@Setter
public class RolVO extends Vo {
    
    private int opciones;
    private String modulo;
    private String asignado;
    
    String codigo;
    
    private List<PermisoVo> permisos;
    
}
