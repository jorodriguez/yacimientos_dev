/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.requisicion.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jcarranza
 */
@Getter
@Setter
public class RequisicionMovimientoVO {
    
    private int id;
    private int idOperacion;
    private String motivo;
    private String usuario;
    private Date fecha;
    private Date hora;
    
}
