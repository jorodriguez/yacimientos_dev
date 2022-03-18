/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import lombok.Data;

/**
 *
 * @author mluis
 */
@Data
public class TipoSolicitudTipoEspecificoVO {
    private int idTipoSolicitudTipoEspecifico;
    private int idTipoSolicitud;
    private int idTipoEspecifico;
    private String tipoEspecifico;
    private String descripcion;
    
}
