package sia.modelo.vo.inventarios;

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
public class ReporteMayoresEntradasYSalidasVO extends Vo {
    private static final long serialVersionUID = 1L;

    private Long articuloId;
    private String articuloCodigo;
    private String articuloNombre;
    private double numeroEntradas;
    private double numeroSalidas;
    private Date fechaInicio;
    private Date fechaFin;

    public ReporteMayoresEntradasYSalidasVO(){

    }

    public ReporteMayoresEntradasYSalidasVO(Long articuloId, String articuloCodigo, String articuloNombre, double numeroEntradas, double numeroSalidas){
        this.articuloId = articuloId;
        this.articuloCodigo = articuloCodigo;
        this.articuloNombre = articuloNombre;
        this.numeroEntradas = numeroEntradas;
        this.numeroSalidas = numeroSalidas;
    }
}
