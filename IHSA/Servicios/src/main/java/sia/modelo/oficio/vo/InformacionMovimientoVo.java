/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.oficio.vo;

import lombok.Data;
import sia.modelo.OfOficio;

/**
 * Contiene la información necesaria para el registro de un movimiento de oficio
 * en la base de datos.
 *
 * @author esapien
 */
public @Data class InformacionMovimientoVo {
    
    private OfOficio oficio;
    
    private Integer operacionId;
    
    private String motivoMovimiento;
    
    private String usuarioId;
    
    
    
    
}
