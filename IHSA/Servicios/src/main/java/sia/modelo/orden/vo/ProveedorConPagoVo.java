/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.orden.vo;

import java.io.Serializable;
import lombok.Data;


/**
 *
 * @author icristobal
 */
@Data
public class ProveedorConPagoVo implements Serializable {
    public Integer id;
    public Integer idProveedor;
    public String nombreProveedor;
    public Integer idConPago;
    public String nombreConPago;   
    public String notificar;
}

