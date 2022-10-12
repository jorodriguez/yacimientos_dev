package sia.modelo.vo.inventarios;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author Aplimovil SA de CV
 */
@Getter
@Setter
public class InventarioMovimientoVO extends Vo implements Serializable {

    private Integer inventarioId;
    private Date fecha;
    private Integer tipoMovimiento;
    private double numeroUnidades; // Este numero es positivo si fue un aumento, o negativo si fue una disminucion
    private Integer transaccionId;
    private String folioRemision;

    public InventarioMovimientoVO() {

    }

    public InventarioMovimientoVO(Integer id, Integer inventarioId, Date fecha,
	    Integer tipoMovimiento, double numeroUnidades, Integer transaccionId,
	    String genero) {
	this.id = id;
	this.inventarioId = inventarioId;
	this.fecha = fecha;
	this.tipoMovimiento = tipoMovimiento;
	this.numeroUnidades = numeroUnidades;
	this.transaccionId = transaccionId;
	this.setGenero(genero);
    }
    
    public InventarioMovimientoVO(Integer id, Integer inventarioId, Date fecha,
	    Integer tipoMovimiento, double numeroUnidades, Integer transaccionId,
	    String genero, String folioRemision) {
	this.id = id;
	this.inventarioId = inventarioId;
	this.fecha = fecha;
	this.tipoMovimiento = tipoMovimiento;
	this.numeroUnidades = numeroUnidades;
	this.transaccionId = transaccionId;
	this.setGenero(genero);
        this.setFolioRemision(folioRemision);
    }
}
