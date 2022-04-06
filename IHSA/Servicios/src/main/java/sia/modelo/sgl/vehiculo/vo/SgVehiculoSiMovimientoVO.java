/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vehiculo.vo;

import lombok.Data;

/**
 *
 * @author nlopez
 */
@Data
public class SgVehiculoSiMovimientoVO {
    private Integer id;
    private String movimiento;
    private String oficinaOrigen;
    private String oficinaDestino;
    
}
