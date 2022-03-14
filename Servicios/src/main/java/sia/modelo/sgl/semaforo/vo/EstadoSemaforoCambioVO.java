/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.semaforo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mluis
 */
@Getter
@Setter
public class EstadoSemaforoCambioVO {
    
    private String ruta;
    private String colorAnterior;
    private String colorNuevo;
    private String descripcion;
}
