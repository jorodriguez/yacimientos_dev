package sia.modelo.vo.inventarios;

import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class TransaccionArticuloVO extends Vo {

    private static final long serialVersionUID = 1L;

    private Integer transaccionId;
    private Integer articuloId;
    private Integer detalleCompraId;
    private String articuloNombre;
    private double numeroUnidades; // Este numero siempre debera ser positivo
    private String identificador;
    private double precioUnitario;
    private int idMoneda;
    private String moneda;  
    private double numeroUnidadesOriginal; // Este numero siempre debera ser positivo
    private boolean selected = true;
    private double totalPendiente;
    private double cantidad;

    public TransaccionArticuloVO() {

    }

    public TransaccionArticuloVO(Integer id) {
	this.id = id;
    }
    
    

    public TransaccionArticuloVO(Integer id, Integer transaccionId, Integer articuloId, String articuloNombre,
	    double numeroUnidades, String identificador) {
	this.id = id;
	this.transaccionId = transaccionId;
	this.articuloId = articuloId;
	this.articuloNombre = articuloNombre;
	this.numeroUnidades = numeroUnidades;
	this.identificador = identificador;
    }
}
