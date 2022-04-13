/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import lombok.Data;

/**
 *
 * @author nlopez
 */
@Data
public class HistorialItinerarioVO {
    private Integer id;
    private String origen;
    private String destino;
    private boolean vigente;
    private String numeroVuelo;
}
