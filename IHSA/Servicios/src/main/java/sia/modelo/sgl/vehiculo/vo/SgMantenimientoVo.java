/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vehiculo.vo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
@Getter
@Setter
public class SgMantenimientoVo extends Vo {

    private int idSgVehiculo;                
    private String nombreSgTipoEspecifico;    
    private Integer idSgTipoEspecifico;        
    private Date fechaIngreso;
    private BigDecimal Importe;
    private Integer kilometraje;
    private Integer kilometrajeProximoMantenimiento;
    private Date fechaProximoMantenimiento;
    private boolean terminado;
    private Integer idAdjunto;
    private String uuid;
    private String nombreAdjunto;
    private String moneda;
    private String nombreProveedor;
    private Integer idProveedor;
    private boolean actual;
    

    

    
    
    
    
}
