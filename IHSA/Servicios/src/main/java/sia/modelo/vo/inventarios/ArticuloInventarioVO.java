package sia.modelo.vo.inventarios;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Esta clase contiene información sobre el artículo indicado y la información
 * de inventarios relacionado al artículo
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class ArticuloInventarioVO extends ArticuloVO {

    private static final long serialVersionUID = 3L;

    private List<InventarioVO> inventarios;

    public ArticuloInventarioVO() {
    }

    public ArticuloInventarioVO(ArticuloVO articuloVO) {
	super(articuloVO.getId(),
		articuloVO.getCodigo(),
		articuloVO.getNombre(),
		articuloVO.getDescripcion(),
		articuloVO.getUnidadId(),
		articuloVO.getUnidadNombre(),
		articuloVO.getCategoriaId());
    }
}
