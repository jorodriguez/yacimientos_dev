/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jorodriguez
 */
/**
 * @descripcion
 * 
 */


@Getter
@Setter
public class DetalleItinerarioCompletoVo extends Vo implements Serializable  {
    private int idCiudadOrigen;
    private String nombreCiudadOrigen;
    private int idCiudadDestino;
    private String nombreCiudadDestino;
    private String numeroVuelo;    
    private String nombreAerolinea;
    private String nombrePaisOrigen;
    private String nombrePaisDestino;
    private String nombreEstadoOrigen;
    private String nombreEstadoDestino;
    private int idAerolinea;
    private Date fechaSalida;
    private Date horaSalida;
    private Date fechaLlegada;
    private Date horaLlegada;
    private Double tiempoVuelo;       
    
    public DetalleItinerarioCompletoVo() {
    }    
}
