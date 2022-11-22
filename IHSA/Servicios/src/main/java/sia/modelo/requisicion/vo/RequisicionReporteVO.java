/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.RequisicionVO;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class RequisicionReporteVO {
    
    private String comprador;
    private List<RequisicionVO> lRequisicion;
    private Double total;
    private long totalRequisiciones;
    private String mes;
    private String cadena;
    private long totalPorMes;
    private long totalAnioAnteriores;
    
    
}
