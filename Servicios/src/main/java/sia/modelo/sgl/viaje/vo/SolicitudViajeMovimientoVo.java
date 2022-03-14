/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class SolicitudViajeMovimientoVo {
    private String cancelo;
    private String operacion;
    private Date fecha;
    private Date hora;
    private String motivo;
}
