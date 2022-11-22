/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sistema.vo.FacturaVo;

/**
 *
 * @author ihsa
 */
@Getter
@Setter
public class ViajeFacturaVo extends FacturaVo implements Serializable {
    private int idViajeFactura;
    private int idViaje;
    private int idViajero;
    private String viajero;
    private String viaje;
    private String tipo;
       private double  total;
    private String gerencia;
 
}
