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
public class RolTipoSolicitudVo {
private int idRolTipoSolicitud;
private int idRol;
private int idTipoSolicitud;
private String nombreRol;
private String tipoSolicitud;   
}
