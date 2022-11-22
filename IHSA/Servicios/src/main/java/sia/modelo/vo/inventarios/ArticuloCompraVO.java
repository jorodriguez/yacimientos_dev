package sia.modelo.vo.inventarios;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class ArticuloCompraVO extends ArticuloVO {
    
    private int idDetalleCompra;
    private double cantidad;

    public ArticuloCompraVO() {
    }

    public ArticuloCompraVO(int idDetalleCompra, Integer id, String nombre, double cantidad, double precioUnitario) {
        super(id, nombre);
        this.idDetalleCompra = idDetalleCompra;
        this.cantidad = cantidad;
        super.setPrecio(precioUnitario);
    }
}
