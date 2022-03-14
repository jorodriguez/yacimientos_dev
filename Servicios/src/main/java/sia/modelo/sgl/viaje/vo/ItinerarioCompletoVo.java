/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jorodriguez
 */
/**
 * Clase Vo que toma los atributos de la tabla Sg_itinerario y anexado a esto toma las escalas del mismo de la tabla dg_detalle_itinerario
 * @descripcion
 * 
 */

@Getter 
@Setter
public class ItinerarioCompletoVo extends Vo implements Serializable{
    private int idCiudadOrigen;
    private String nombreCiudadOrigen;
    private int idCiudadDestino;
    private String nombreCiudadDestino;
    private boolean ida;   
    private boolean notificado;   
    private List<DetalleItinerarioCompletoVo> escalas;

    public ItinerarioCompletoVo() {
    }

    
}
