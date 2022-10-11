package  sia.modelo.vo.inventarios;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class UnidadVO extends Vo {
    private static final long serialVersionUID = 1L;

    private String descripcion;

    public UnidadVO(){
    }

    public UnidadVO(Integer id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
